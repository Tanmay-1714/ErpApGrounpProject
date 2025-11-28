compile:
	echo 'javac -d bin -cp "lib/*" $$(find src -name "*.java")'
	javac -d bin -cp "lib/*" $$(find src -name "*.java")

test:
	java -jar lib/junit-platform-console-standalone-1.11.3.jar \
		--classpath "bin:lib/jbcrypt-0.4.jar:lib/mysql-connector-j-9.5.0.jar" \
		--scan-classpath

run:
	java -cp "bin:lib/*" Main
