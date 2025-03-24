Sistema de Controle de Presença com Arduino e Java📌 
Sobre o Projeto
Este projeto integra Arduino e Java para criar um sistema de controle de presença. O Arduino detecta movimentos e emite sinais sonoros quando alguém se aproxima. O Java recebe esses sinais, registra os eventos com data e horário em um banco de dados e permite a análise por meio de filtros e geração de gráficos.

🛠️ Tecnologias Utilizadas
Arduino (com sensor de presença - PIR, ultrassônico, etc.)

Java (JDK 11 ou superior)

MySQL

JDBC (para conexão com o banco de dados)

Libraria JSSC (para comunicação serial entre Arduino e Java)

JFreeChart (para geração de gráficos)

Swing (para a interface gráfica)

🎯 Funcionalidades
🚀 Detecção de presença via Arduino

🔊 Emissão de alerta sonoro quando houver movimento

🔄 Envio do sinal para o Java via comunicação serial

📊 Armazenamento de eventos no banco de dados (data e horário)

📆 Filtragem dos registros por dia, hora, mês e ano

📈 Geração de gráficos com os dados coletados

🖥️ Interface gráfica em Swing, exibindo os registros em tempo real em uma tabela interativa

🔍 Filtros na interface para selecionar registros específicos

📂 Estrutura do Projeto
css
Copiar
Editar
📦 .idea
📦 gradle
📦 src
 ┣ 📂 main
 ┃ ┣ 📂 java
 ┃ ┃ ┣ 📂 dao
 ┃ ┃ ┃ ┣ 📜 ArduinoDAO.java
 ┃ ┃ ┃ ┣ 📜 GraficoDAO.java
 ┃ ┃ ┣ 📂 model
 ┃ ┃ ┃ ┣ 📜 ComandoPorDia.java
 ┃ ┃ ┃ ┣ 📜 ConexaoArduino.java
 ┃ ┃ ┃ ┣ 📜 GraficoTela.java
 ┃ ┃ ┃ ┣ 📜 Main.java
 ┃ ┃ ┃ ┣ 📜 Registro.java
 ┃ ┃ ┣ 📂 util
 ┃ ┃ ┃ ┣ 📜 ConnectionFactory.java
 ┃ ┃ ┃ ┣ 📜 DateLabelFormatter.java
 ┃ ┃ ┃ ┣ 📜 NonEditableTableModel.java
 ┃ ┃ ┣ 📂 view
 ┃ ┃ ┃ ┣ 📜 InterfacePrincipal.java
 ┣ 📜 .gitignore
 ┣ 📜 bancopreenchido.sql
 ┣ 📜 build.gradle.kts
 ┣ 📜 codigo_arduino_sensor.ino
 ┣ 📜 gradlew
 ┣ 📜 gradlew.bat
 ┣ 📜 settings.gradle.kts
💻 Código Arduino
O código a seguir é responsável pela detecção de movimento utilizando um sensor PIR e acionamento de um buzzer. Quando o movimento é detectado, o Arduino envia uma mensagem para o Java e emite um sinal sonoro.

cpp
Copiar
Editar
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

⚙️ Como Executar o Projeto
🔧 Arduino
Conecte o sensor de presença ao Arduino.

Carregue o código codigo_arduino_sensor.ino na placa via Arduino IDE.

Verifique a porta COM utilizada e configure no código.

💻 Java
Clone este repositório:

bash
Copiar
Editar
git clone https://github.com/Lucas01012/Arduino-Java-Controle_Presenca.git
Configure as credenciais do banco no arquivo ConnectionFactory.java.

Instale as bibliotecas RXTX ou JSSC para comunicação serial.

Execute o projeto a partir da classe Main.java.

Acesse a interface para visualizar os registros, aplicar filtros e gerar gráficos.

📌 Autor
Desenvolvido por Lucas Oliveira Silva

✉️ Para dúvidas ou sugestões, entre em contato!
