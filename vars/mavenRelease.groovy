def call(RELVER)
{
sh '''
    pom=`/var/lib/jenkins/tools/hudson.tasks.Maven_MavenInstallation/Maven_3.3.9/bin/mvn org.apache.maven.plugins:maven-help-plugin:3.1.0:evaluate -Dexpression=project.version -q -DforceStdout`
    #version=pom.version.replace("-SNAPSHOT", ".${RELVER}")
    newversion=`echo $pom | cut -f1 -d"-"`
    version=`/var/lib/jenkins/tools/hudson.tasks.Maven_MavenInstallation/Maven_3.3.9/bin/mvn versions:set -DnewVersion=${newversion}-${RELVER}`
    /var/lib/jenkins/tools/hudson.tasks.Maven_MavenInstallation/Maven_3.3.9/bin/mvn -DreleaseVersion=${version} -DdevelopmentVersion=${pom} -DpushChanges=false -DlocalCheckout=true -DpreparationGoals=initialize release:prepare release:perform -B
    ''' 
}
