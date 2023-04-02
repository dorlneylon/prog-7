cd chunked_sender
mvn -B package
cd ../client
bash ../mkdep.sh
mvn clean package
cd ../server
bash ../mkdep.sh
mvn clean package
