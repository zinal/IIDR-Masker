#! /bin/sh

# MacOS options:
# export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0_181.jdk/Contents/Home
# MVN="/Applications/NetBeans/NetBeans 8.2.app/Contents/Resources/NetBeans/java/maven/bin/mvn"

# Debian Linux options:
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
MVN=/opt/netbeans/v12.5/netbeans/java/maven/bin/mvn

"$MVN" --version

XVER=11.4.0.4.5607
XLOC=/home/zinal/Software/iidr-pg/lib

# Buildtime dependency jars:
# * ts

for jbase in ts; do
  echo "Installing $jbase ..."

  "$MVN" install:install-file -Dfile="$XLOC"/"$jbase".jar -Dpackaging=jar \
      -DgroupId=com.ibm.iidr -DartifactId="$jbase" -Dversion="$XVER" 

done
