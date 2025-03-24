Sistema de Controle de Presença com Arduino e Java

📌 Sobre o Projeto

Este projeto integra Arduino e Java para criar um sistema de controle de presença. O Arduino detecta movimentos e emite sinais sonoros quando alguém se aproxima. O Java recebe esses sinais, registra os eventos com data e horário em um banco de dados e permite a análise por meio de filtros e geração de gráficos.

🛠️ Tecnologias Utilizadas

Arduino (com sensor de presença - PIR, ultrassônico, etc.)

Java (JDK 11 ou superior)

MySQL

JDBC (para conexão com o banco de dados)

Libraria RXTX ou JSSC (para comunicação serial entre Arduino e Java)

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

📦 src
 ┣ 📂 arduino
 ┃ ┣ 📜 codigo_arduino.ino
 ┣ 📂 dao
 ┃ ┣ 📜 PresencaDAO.java
 ┣ 📂 model
 ┃ ┣ 📜 RegistroPresenca.java
 ┣ 📂 util
 ┃ ┣ 📜 ConnectionFactory.java
 ┣ 📂 view
 ┃ ┣ 📜 Dashboard.java
 ┃ ┣ 📜 GraficoPresenca.java
 ┃ ┣ 📜 InterfaceSwing.java
 ┣ 📂 controller
 ┃ ┣ 📜 PresencaController.java
 ┣ 📜 script.sql
 ┣ 📜 .gitignore
 ┣ 📜 README.md

⚙️ Como Executar o Projeto

🔧 Arduino

Conecte o sensor de presença ao Arduino.

Carregue o código codigo_arduino.ino na placa via Arduino IDE.

Verifique a porta COM utilizada e configure no código.

💻 Java

Clone este repositório:

git clone https://github.com/seuusuario/seuprojeto.git

Configure as credenciais do banco no arquivo ConnectionFactory.java.

Instale as bibliotecas RXTX ou JSSC para comunicação serial.

Execute o projeto a partir da classe main.

Acesse a interface para visualizar os registros, aplicar filtros e gerar gráficos.


📌 Autor

Desenvolvido por Lucas Oliveira Silva
