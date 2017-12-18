#include <GSM.h>

#define APIKEY "16W5PIT7CK6XUN8N" 

#define PINNUMBER "" // PIN NUMBER 

#define GPRS_APN "sl2sfr"
#define GPRS_LOGIN ""
#define GPRS_PASSWORD ""

// définit les numéros de pins
const int trigPin = 9;
const int echoPin = 10;
// defines variables
long duration;
int distanceC;

GSMClient client;       //inisialisation des instances de connexion
GPRS gprs;              //se connecter à des données mobiles
GSM gsmAccess;          //envoyez-nous un ok pour se connecter au GSM
GSM_SMS sms;            //pour envoyer des données sms
//GSMModem modem;

//////Données pour le serveur
char server[]= "api.thingspeak.com";
//////variables des connexion
unsigned long lastConnectionTime=0;
boolean lastConnected=false;
const unsigned long postingInterval= 298000;
/////Données SMS
char numero [10]="0651033763";  // Ma 2émme ligne free mobile
char txtMsg [100];      //tableau des caractéres pour envoyer une sms

//Déclaration des variables de capteur de couleur TCS230
const int s0 = 8;  
const int s1 = 9;  
const int s2 = 12;  
const int s3 = 11;  
const int out = 10;   
// LED pins connected to Arduino
int redLed = 2;  
int greenLed = 3;  
int blueLed = 4;
// Variables  
int red = 0;  
int green = 0;  
int blue = 0; 


void setup() {
  //Lecture des INPUTS OUTPUTS de notre capteur de couleur
    pinMode(s0, OUTPUT);  
  pinMode(s1, OUTPUT);  
  pinMode(s2, OUTPUT);  
  pinMode(s3, OUTPUT);  
  pinMode(out, INPUT);  
  pinMode(redLed, OUTPUT);  
  pinMode(greenLed, OUTPUT);  
  pinMode(blueLed, OUTPUT);  
  digitalWrite(s0, HIGH);  
  digitalWrite(s1, HIGH); 
  // put your setup code here, to run once:
  Serial.begin(9600);
  pinMode(13,OUTPUT);
  pinMode(trigPin, OUTPUT); // Sets the trigPin as an Output
pinMode(echoPin, INPUT); // Sets the echoPin as an Input

  while (!Serial) {
    ; // wait for serial port to connect. Needed for native USB port only
  }
  Serial.println("Starting Arduino web client.");

  //Cette partie est nécessaire pour commencer puisqu'elle nous informe que nous sommes connectés
  boolean notConnected=true;
  while(notConnected){
      if((gsmAccess.begin(PINNUMBER)==GSM_READY)&&
          (gprs.attachGPRS(GPRS_APN, GPRS_LOGIN, GPRS_PASSWORD)==GPRS_READY)){
            notConnected = false;}
      else{
          Serial.println("Not connected");
          delay(1000); 
        }
    }
    Serial.println("GSM inicializado a la red");  
}

void loop() {
// Clears the trigPin
digitalWrite(trigPin, LOW);
delayMicroseconds(2);
// Sets the trigPin on HIGH state for 10 micro seconds
digitalWrite(trigPin, HIGH);
delayMicroseconds(10);
digitalWrite(trigPin, LOW);
// Reads the echoPin, returns the sound wave travel time in microseconds
duration = pulseIn(echoPin, HIGH);
// Calculating the distance
distanceC= duration*0.034/2;
// Prints the distance on the Serial Monitor
String distance= String(distanceC);
//Traitement des objets de conteneur
// Définition de compteur de chaque type
long compteurRed=0;
long compteurBlue=0;
long compteurGreen=0;
color(); 
  Serial.print("R Intensity:");  
  Serial.print(red, DEC);  
  Serial.print(" G Intensity: ");  
  Serial.print(green, DEC);  
  Serial.print(" B Intensity : ");  
  Serial.print(blue, DEC);
  //Serial.println();  

  if (red < blue && red < green && red < 20)
  {  
    compteurRed=compteurRed+1;
   Serial.println(" - (Red Color)");  
   digitalWrite(redLed, HIGH); // Turn RED LED ON 
   digitalWrite(greenLed, LOW);  
   digitalWrite(blueLed, LOW);  
  }  

  else if (blue < red && blue < green)   
  {  
    compteurBlue=compteurBlue+1;
   Serial.println(" - (Blue Color)");  
   digitalWrite(redLed, LOW);  
   digitalWrite(greenLed, LOW);  
   digitalWrite(blueLed, HIGH); // Turn BLUE LED ON  
  }  

  else if (green < red && green < blue)  
  {  
    compteurGreen=compteurGreen+1;
   Serial.println(" - (Green Color)");  
   digitalWrite(redLed, LOW);  
   digitalWrite(greenLed, HIGH); // Turn GREEN LED ON 
   digitalWrite(blueLed, LOW);  
  }  
  else{
  Serial.println();  
  }
  delay(1000);   
  digitalWrite(redLed, LOW);  
  digitalWrite(greenLed, LOW);  
  digitalWrite(blueLed, LOW); 



String compteurRedC= String(compteurRed);
String compteurBlueC= String(compteurBlue);
String compteurGreenC= String(compteurGreen);
// Contenu du message
  sprintf(txtMsg, "Bonjour, Votre conteneur est presque plein",compteurRedC.c_str(),compteurBlueC.c_str(),compteurGreenC.c_str(), distance.c_str());
 //Connexion au serveur.
  if (client.available()){
    char c= client.read();
    Serial.print(c);
  }
  if (!client.connected() && lastConnected){
    client.stop();  
  }
  // 
  if (!client.connected() && ((millis()-lastConnectionTime)>postingInterval)){
    if (distanceC<5) sendSMS();
    sendData("field1="+distance); 
   }
  lastConnected=client.connected();
}

//Données de connexion au serveur. 
void sendData(String ThisData){
  if (client.connect(server,80)) {
    Serial.println("connecting...");
    client.println("POST /update HTTP/1.1");
    client.println("Host: api.thingspeak.com");
    client.println("Connection: close");
    client.print("X-THINGSPEAKAPIKEY:");
    client.println(APIKEY);
    client.println("Content-Type: application/x-www-form-urlencoded");
    client.print("Content-Length:");
    client.println(ThisData.length());
    client.println();
    client.println(ThisData);
  }
  else {
    Serial.println("connection failed");
    Serial.println();
    Serial.println("disconnecting");
    client.stop();
  }
  lastConnectionTime=millis();
}
//Fonction pour Notifier avec SMS
void sendSMS () { 

    Serial.print("Distance: ");
    Serial.print(distanceC);
    sms.beginSMS(numero); //instance dans le numéro qui va envoyer le message
    sms.print(txtMsg);//instance pour imprimer TXTMSG
    sms.endSMS();   //instance pour envoyer un message.
    digitalWrite(13,HIGH);
    delay(1000); //le temps nécessaire pour faire passer le message.
    digitalWrite(13,LOW);
    sms.flush();
    Serial.println("\nCOMPLETE!\n");
    }
    //Fonction de reconnaissance de couleur introduit dans le conteneur
    void color()  
{    
  digitalWrite(s2, LOW);  
  digitalWrite(s3, LOW);  
  //count OUT, pRed, RED  
  red = pulseIn(out, digitalRead(out) == HIGH ? LOW : HIGH);  
  digitalWrite(s3, HIGH);  
  //count OUT, pBLUE, BLUE  
  blue = pulseIn(out, digitalRead(out) == HIGH ? LOW : HIGH);  
  digitalWrite(s2, HIGH);  
  //count OUT, pGreen, GREEN  
  green = pulseIn(out, digitalRead(out) == HIGH ? LOW : HIGH);  
}
  

