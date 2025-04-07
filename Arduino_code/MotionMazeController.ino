/**
 * Motion Maze Controller
 * 
 * This Arduino sketch runs on a Lilypad Arduino and handles:
 * 1. Reading MPU6050 accelerometer data for tilt-based control
 * 2. Reading push button input for game control
 * 3. Reading motion sensor input for dynamic gameplay
 * 4. Controlling RGB LED for visual feedback
 * 5. Controlling buzzer for audio feedback
 * 6. Controlling servo for physical feedback and maze element control
 * 7. Communicating with Java application via ZigBee
 */

// Include necessary libraries
#include <Wire.h>
#include <MPU6050.h>  // MPU6050 accelerometer and gyroscope library
#include <Servo.h>    // Servo motor library

// Constants
#define BUTTON_PIN 2
#define MOTION_SENSOR_PIN 3
#define RED_PIN 9
#define GREEN_PIN 10
#define BLUE_PIN 11
#define BUZZER_PIN 6
#define SERVO_PIN 5

// ZigBee communication
#define ZIGBEE_RX 0
#define ZIGBEE_TX 1

// MPU6050 sensitivity thresholds
#define TILT_THRESHOLD 2000

// Debounce time for button (ms)
#define DEBOUNCE_TIME 200

// Game movement codes
#define MOVE_UP 1
#define MOVE_RIGHT 2
#define MOVE_DOWN 3
#define MOVE_LEFT 4
#define BUTTON_PRESS 5
#define MOTION_DETECT 6

// LED colors
#define LED_RED 0
#define LED_GREEN 1
#define LED_BLUE 2
#define LED_YELLOW 3
#define LED_CYAN 4
#define LED_MAGENTA 5
#define LED_WHITE 6
#define LED_OFF 7

// Servo positions
#define SERVO_MIN 0      // 0 degrees
#define SERVO_CENTER 90  // 90 degrees
#define SERVO_MAX 180    // 180 degrees

// Objects
MPU6050 mpu;
Servo gameServo;

// Variables
int16_t ax, ay, az;               // Accelerometer values
uint8_t lastMovement = 0;         // Last movement sent
uint8_t lastButtonState = HIGH;   // Last button state (pull-up)
uint8_t lastMotionState = LOW;    // Last motion sensor state
unsigned long lastDebounceTime = 0;  // Last time the button was debounced
unsigned long lastMotionTime = 0;    // Last time motion was detected
unsigned long lastMpuReadTime = 0;   // Last time the MPU was read
unsigned long lastColorChangeTime = 0; // Last time LED color was changed
unsigned long servoReturnTime = 0;    // Time to return servo to center position
boolean pendingServoReturn = false;   // Flag to return servo to center

// Function prototypes
void setLedColor(uint8_t color);
void playTone(uint8_t tone);
void moveServo(uint8_t position);
void sendZigBeeData(uint8_t data);
uint8_t receiveZigBeeData();
void processReceivedData(uint8_t data);
void handleServoBarrier(boolean isOpen);

void setup() {
  // Initialize serial for debugging
  Serial.begin(9600);
  Serial.println("Motion Maze Controller initializing...");
  
  // Initialize I2C for MPU6050
  Wire.begin();
  
  // Initialize MPU6050
  while (!mpu.begin(MPU6050_SCALE_2000DPS, MPU6050_RANGE_16G)) {
    Serial.println("Could not find a valid MPU6050 sensor, check wiring!");
    delay(500);
  }
  
  // Initialize pins
  pinMode(BUTTON_PIN, INPUT_PULLUP);
  pinMode(MOTION_SENSOR_PIN, INPUT);
  pinMode(RED_PIN, OUTPUT);
  pinMode(GREEN_PIN, OUTPUT);
  pinMode(BLUE_PIN, OUTPUT);
  pinMode(BUZZER_PIN, OUTPUT);
  
  // Initialize servo
  gameServo.attach(SERVO_PIN);
  gameServo.write(SERVO_CENTER);  // Center position
  
  // Set initial LED color
  setLedColor(LED_OFF);
  
  // Play startup tone
  playTone(1);
  
  Serial.println("Motion Maze Controller initialized");
}

void loop() {
  // Read accelerometer data at regular intervals
  unsigned long currentTime = millis();
  if (currentTime - lastMpuReadTime > 100) {  // Read every 100ms
    lastMpuReadTime = currentTime;
    
    // Read accelerometer data
    Vector normAccel = mpu.readNormalizeAccel();
    ax = normAccel.XAxis;
    ay = normAccel.YAxis;
    az = normAccel.ZAxis;
    
    // Determine tilt direction
    uint8_t movement = 0;
    
    if (ax > TILT_THRESHOLD) {
      movement = MOVE_RIGHT;
    } else if (ax < -TILT_THRESHOLD) {
      movement = MOVE_LEFT;
    } else if (ay > TILT_THRESHOLD) {
      movement = MOVE_DOWN;
    } else if (ay < -TILT_THRESHOLD) {
      movement = MOVE_UP;
    }
    
    // Send movement data if it changed
    if (movement != 0 && movement != lastMovement) {
      lastMovement = movement;
      sendZigBeeData(movement);
      
      // Move servo slightly based on movement
      moveServoForMovement(movement);
      
      // Debug
      Serial.print("Movement: ");
      Serial.println(movement);
    }
  }
  
  // Read button state
  uint8_t buttonState = digitalRead(BUTTON_PIN);
  
  // Debounce button
  if (buttonState != lastButtonState && (currentTime - lastDebounceTime) > DEBOUNCE_TIME) {
    lastDebounceTime = currentTime;
    lastButtonState = buttonState;
    
    if (buttonState == LOW) {  // Button pressed (pull-up)
      sendZigBeeData(BUTTON_PRESS);
      
      // Flash LED
      setLedColor(LED_CYAN);
      
      // Debug
      Serial.println("Button pressed");
    } else {
      // Turn LED off
      setLedColor(LED_OFF);
    }
  }
  
  // Read motion sensor
  uint8_t motionState = digitalRead(MOTION_SENSOR_PIN);
  
  // Debounce motion sensor
  if (motionState != lastMotionState && (currentTime - lastMotionTime) > 1000) {  // 1 second debounce
    lastMotionTime = currentTime;
    lastMotionState = motionState;
    
    if (motionState == HIGH) {  // Motion detected
      sendZigBeeData(MOTION_DETECT);
      
      // Flash LED
      setLedColor(LED_WHITE);
      
      // Debug
      Serial.println("Motion detected");
    } else {
      // Turn LED off after motion stops
      setLedColor(LED_OFF);
    }
  }
  
  // Check for incoming ZigBee data
  uint8_t receivedData = receiveZigBeeData();
  if (receivedData != 0) {
    processReceivedData(receivedData);
  }
  
  // Check if it's time to return servo to center position
  if (pendingServoReturn && currentTime > servoReturnTime) {
    pendingServoReturn = false;
    gameServo.write(SERVO_CENTER);
  }
}

/**
 * Move servo based on movement direction.
 * 
 * @param direction Movement direction (1-4)
 */
void moveServoForMovement(uint8_t direction) {
  int position = SERVO_CENTER;
  
  switch (direction) {
    case MOVE_UP:
      position = SERVO_CENTER - 20;
      break;
    case MOVE_RIGHT:
      position = SERVO_CENTER + 30;
      break;
    case MOVE_DOWN:
      position = SERVO_CENTER + 20;
      break;
    case MOVE_LEFT:
      position = SERVO_CENTER - 30;
      break;
  }
  
  // Move servo
  gameServo.write(position);
  
  // Schedule return to center
  pendingServoReturn = true;
  servoReturnTime = millis() + 300;  // Return after 300ms
}

/**
 * Handle servo barrier function - used for blocking/opening paths in the maze.
 * 
 * @param isOpen Whether the barrier is open (true) or closed (false)
 */
void handleServoBarrier(boolean isOpen) {
  if (isOpen) {
    // Open barrier (move to one side)
    gameServo.write(SERVO_MIN);
  } else {
    // Close barrier (move to blocking position)
    gameServo.write(SERVO_MAX);
  }
}

/**
 * Process data received from ZigBee.
 * Format: [type][value]
 * Type 1: LED control
 * Type 2: Buzzer control
 * Type 3: Servo control
 */
void processReceivedData(uint8_t data) {
  uint8_t type = data >> 4;    // First 4 bits
  uint8_t value = data & 0x0F; // Last 4 bits
  
  Serial.print("Received data - Type: ");
  Serial.print(type);
  Serial.print(", Value: ");
  Serial.println(value);
  
  switch (type) {
    case 1:  // LED control
      setLedColor(value);
      break;
    case 2:  // Buzzer control
      playTone(value);
      break;
    case 3:  // Servo control
      moveServo(value);
      break;
  }
}

/**
 * Set the RGB LED color.
 * 
 * @param color Color code
 */
void setLedColor(uint8_t color) {
  switch (color) {
    case LED_RED:
      analogWrite(RED_PIN, 255);
      analogWrite(GREEN_PIN, 0);
      analogWrite(BLUE_PIN, 0);
      break;
    case LED_GREEN:
      analogWrite(RED_PIN, 0);
      analogWrite(GREEN_PIN, 255);
      analogWrite(BLUE_PIN, 0);
      break;
    case LED_BLUE:
      analogWrite(RED_PIN, 0);
      analogWrite(GREEN_PIN, 0);
      analogWrite(BLUE_PIN, 255);
      break;
    case LED_YELLOW:
      analogWrite(RED_PIN, 255);
      analogWrite(GREEN_PIN, 255);
      analogWrite(BLUE_PIN, 0);
      break;
    case LED_CYAN:
      analogWrite(RED_PIN, 0);
      analogWrite(GREEN_PIN, 255);
      analogWrite(BLUE_PIN, 255);
      break;
    case LED_MAGENTA:
      analogWrite(RED_PIN, 255);
      analogWrite(GREEN_PIN, 0);
      analogWrite(BLUE_PIN, 255);
      break;
    case LED_WHITE:
      analogWrite(RED_PIN, 255);
      analogWrite(GREEN_PIN, 255);
      analogWrite(BLUE_PIN, 255);
      break;
    case LED_OFF:
      analogWrite(RED_PIN, 0);
      analogWrite(GREEN_PIN, 0);
      analogWrite(BLUE_PIN, 0);
      break;
  }
}

/**
 * Play a tone on the buzzer.
 * 
 * @param tone Tone code
 */
void playTone(uint8_t tone) {
  switch (tone) {
    case 1:  // Collision sound
      analogWrite(BUZZER_PIN, 128);
      delay(100);
      analogWrite(BUZZER_PIN, 0);
      break;
    case 2:  // Power-up sound
      for (int i = 0; i < 3; i++) {
        analogWrite(BUZZER_PIN, 100 + i * 50);
        delay(50);
      }
      analogWrite(BUZZER_PIN, 0);
      break;
    case 3:  // Win sound
      for (int i = 0; i < 5; i++) {
        analogWrite(BUZZER_PIN, 100 + i * 30);
        delay(100);
      }
      analogWrite(BUZZER_PIN, 0);
      break;
    case 4:  // Level complete sound
      for (int i = 0; i < 3; i++) {
        analogWrite(BUZZER_PIN, 200);
        delay(100);
        analogWrite(BUZZER_PIN, 0);
        delay(50);
      }
      break;
    case 5:  // Game over sound
      analogWrite(BUZZER_PIN, 200);
      delay(300);
      analogWrite(BUZZER_PIN, 100);
      delay(500);
      analogWrite(BUZZER_PIN, 0);
      break;
  }
}

/**
 * Move the servo to a position.
 * 
 * @param position Position code (0-15)
 */
void moveServo(uint8_t position) {
  // Convert position code to angle (0-180)
  uint8_t angle = map(position, 0, 15, 0, 180);
  gameServo.write(angle);
  
  // For temporary positions, schedule a return to center
  if (position != 7) { // 7 maps to ~90 degrees (center)
    pendingServoReturn = true;
    servoReturnTime = millis() + 500;  // Return after 500ms
  } else {
    pendingServoReturn = false;
  }
}

/**
 * Send data via ZigBee.
 * 
 * @param data Data to send
 */
void sendZigBeeData(uint8_t data) {
  // In a real implementation, send data to ZigBee module
  // For now, just print to serial
  Serial.print("Sending ZigBee data: ");
  Serial.println(data);
}

/**
 * Receive data via ZigBee.
 * 
 * @return Received data (0 if none)
 */
uint8_t receiveZigBeeData() {
  // In a real implementation, receive data from ZigBee module
  // For now, check if there's data available on Serial and use that
  if (Serial.available() > 0) {
    return Serial.read();
  }
  return 0;
}
