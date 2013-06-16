#include <buttons.h>

#include <phi_interfaces.h>


#define Encoder1ChnA 10
#define Encoder1ChnB 11
#define EncoderDetent 24

char mapping1[]={'U','D'}; // This is a rotary encoder so it returns U for up and D for down on the dial.
phi_rotary_encoders my_encoder1(mapping1, Encoder1ChnA, Encoder1ChnB, EncoderDetent);
multiple_button_input* dial1=&my_encoder1;

Button knob(Memory);



//define 6 buttons

Button start(Memory);
Button option(Memory);
Button pause(Memory);
Button bolus(Memory);
Button power(Memory) ;
Button undo (Memory);

void setup()
{
  Serial.begin(9600);  
  knob.assign(12, DIGITAL);
  resetKnob();
  
  start.assign(A5, DIGITAL);
  option.assign(A4, DIGITAL);
  pause.assign(A3, DIGITAL);
  bolus.assign(A2, DIGITAL);
  power.assign(A1, DIGITAL);
  undo.assign(A0, DIGITAL);
}

void resetKnob(){
  digitalWrite(12, HIGH);
}

void checkKnob(){
  switch(knob.check()){
  case Pressed:
    p("knob released");
    break;
  case Released:
    p("knob pressed");
    break;
  default:
    break;
  } 
  resetKnob();
}

void loop()
{
  char temp;
  //Rotary encoder 1:  
  //  temp=my_encoder1.getKey(); // Use phi_keypads object to access the keypad
  temp=dial1->getKey(); // Use the phi_interfaces to access the same keypad
  if (temp!=NO_KEY) 
    Serial.println(temp);

   checkKnob();
  //check the 6 buttons
  checkStart();
  checkOption();
  checkPause();
  checkBolus();
  checkPower();
  checkUndo();
}

void p(char* str){
  Serial.println(str); 
}


void checkStart(){
  switch(start.check()){
  case Pressed:
    p("start pressed");
    break;
  case Released:
    p("start released");
    break;
  default:
    break;
  } 
}

void checkOption(){
  switch(option.check()){
  case Pressed:
    p("option pressed");
    break;
  case Released:
    p("option released");
    break;
  } 
}

void checkPause(){
  switch(pause.check()){
  case Pressed:
    p("pause pressed");
    break;
  case Released:
    p("pause released");
    break;
  } 
}

void checkBolus(){
  switch(bolus.check()){
  case Pressed:
    p("bolus pressed");
    break;
  case Released:
    p("bolus released");
    break;
  } 
}

void checkPower(){
  switch(power.check()){
  case Pressed:
    p("power pressed");
    break;
  case Released:
    p("power released");
    break;
  } 
}


void checkUndo(){
  switch(undo.check()){
  case Pressed:
    p("undo pressed");
    break;
  case Released:
    p("undo released");
    break; 
  }
}



