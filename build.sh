cd chunked_sender
mvn -B package
cd ../client
mvn clean package
cd ../server
mvn clean package
