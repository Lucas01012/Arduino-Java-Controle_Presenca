const int pirPin = 8; 
const int buzzerPin = 10;
const unsigned long beepDuracao = 200;
bool motionDetected = false;

void setup() {
  pinMode(pirPin, INPUT);
  pinMode(buzzerPin, OUTPUT);
  Serial.begin(9600);
}

void loop() {
  int sensorValue = digitalRead(pirPin);

  if (sensorValue == HIGH && !motionDetected) {   
    motionDetected = true;
    Serial.println("Movimento detectado!");  
    tone(buzzerPin, 1000);
    delay(beepDuracao);
    noTone(buzzerPin);
  } 
  else if (sensorValue == LOW && motionDetected) { 
    motionDetected = false;
  }
  
  delay(100); 
}