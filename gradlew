#!/bin/sh
APP_HOME=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd -P) || exit
CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar
if [ -n "$JAVA_HOME" ]; then JAVACMD=$JAVA_HOME/bin/java; else JAVACMD=$(command -v java 2>/dev/null || true); fi
if [ -z "$JAVACMD" ] || [ ! -x "$JAVACMD" ]; then echo "ERROR: JAVA_HOME is not set correctly or java is not on PATH." >&2; exit 1; fi
exec "$JAVACMD" -Xmx64m -Xms64m -Dorg.gradle.appname=gradlew -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"
