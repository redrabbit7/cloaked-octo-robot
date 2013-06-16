#include <buttons.h>

/* @file EventSerialKeypad.pde
|| @version 1.0
|| @author Alexander Brevig
|| @contact alexanderbrevig@gmail.com
||
|| @description
|| | Demonstrates using the KeypadEvent.
|| #
*/

#include <Keypad.h>
#include <string.h>



const byte ROWS = 4; //four rows
const byte COLS = 4; //four columns
//define the cymbols on the buttons of the keypads
char keys[ROWS][COLS] = {
  {'1','2','3','W'},
  {'4','5','6','X'},
  {'7','8','9','Y'},
  {'.','0','C','Z'}
};



byte rowPins[ROWS] = {6,7,8,9}; //connect to the row pinouts of the keypad
byte colPins[COLS] = {5,4,3,2}; //connect to the column pinouts of the keypad

Keypad keypad = Keypad( makeKeymap(keys), rowPins, colPins, ROWS, COLS );

//define 6 buttons

Button start;
Button option;
Button pause;
Button bolus;
Button power;
Button undo;

void init6Buttons(){
  start.setMode( Memory);
  start.assign(5, ANALOG);
  
  option.setMode(Memory);
  option.assign(4, ANALOG);
  
  pause.setMode(Memory);
  pause.assign(3, ANALOG);
  
  bolus.setMode(Memory);
  bolus.assign(2, ANALOG);
  
  power.setMode(Memory);
  power.assign(1, ANALOG);
  
  undo.setMode(Memory);
  undo.assign(0, ANALOG);
}

void p(char* str){
   Serial.println(str); 
}

void setup(){
  Serial.begin(9600);
  init6Buttons();
  keypad.addEventListener(keypadEvent); //add an event listener for this keypad
}
  
void loop(){
  char key = keypad.getKey();
  //check the 6 buttons
  checkStart();
  checkOption();
  checkPause();
  checkBolus();
  checkPower();
  checkUndo();
}

void checkStart(){
   switch(start.check()){
      case Pressed:
           p("start pressed");
      break;
      case Released:
            p("start released");
      break;
      default:break;
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
//take care of some special events
void keypadEvent(KeypadEvent key){ 
  char strKey[20] = {key,'\0'};
  switch (keypad.getState()){
    case PRESSED:
      Serial.println(strcat(strKey," PRESSED"));
    break;
    case RELEASED:
      Serial.println(strcat(strKey, " RELEASED"));
    break;
  }
}
