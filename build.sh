VERSION="0.2"
FILENAME="velocity.editor-$VERSION-jar-with-dependencies.jar"

echo $FILENAME

#!/bin/bash
#build single jar
mvn clean compile assembly:single

cp -R resource/bundle/bundle_folder.app target/VelocityEditor.app

cp "target/$FILENAME" target/VelocityEditor.app/Contents/Resources/app.jar
#ln -sf "target/VelocityEditor.app/Contents/Resources/$FILENAME" "target/VelocityEditor.app/Contents/Resources/app.jar"