mvn install:install-file \
   -Dfile=../chunked_sender/target/chunker-1.0.jar \
   -DgroupId=itmo.chunker \
   -DartifactId=chunker \
   -Dversion=1.0 \
   -Dpackaging=jar \
   -DgeneratePom=true
