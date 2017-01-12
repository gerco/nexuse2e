node {
  stage('Checkout') {

    checkout scm
  }
  
  def mvnHome = tool 'M3'
  def runTests = true
  
  stage('Build Parent') {
    configFileProvider(
      [configFile(fileId: 'mvn-dg-global-settings', variable: 'MAVEN_SETTINGS')]) {
      sh "${mvnHome}/bin/mvn -s $MAVEN_SETTINGS -f nexuse2e-parent/pom.xml -Dmaven.wagon.http.ssl.insecure=true -Dmaven.test.failure.ignore clean install"
    }
  }

  stage('Build Core') {
    configFileProvider(
      [configFile(fileId: 'mvn-dg-global-settings', variable: 'MAVEN_SETTINGS')]) {
      sh "${mvnHome}/bin/mvn -s $MAVEN_SETTINGS -f nexuse2e-core/pom.xml -Dmaven.wagon.http.ssl.insecure=true -Dmaven.test.failure.ignore clean install"
      }
  }

  stage('Build Webapp') {
    configFileProvider(
      [configFile(fileId: 'mvn-dg-global-settings', variable: 'MAVEN_SETTINGS')]) {
      sh "${mvnHome}/bin/mvn -s $MAVEN_SETTINGS -f nexuse2e-webapp/pom.xml -Dmaven.wagon.http.ssl.insecure=true -Dmaven.test.failure.ignore clean package"
    }
  }
  
  stage('Archive Artifacts / Test Results') {
    archiveArtifacts artifacts: '**/nexuse2e-webapp/target/*.war, **/nexuse2e-core/target/*.jar', fingerprint: true
    if (runTests) {
      junit healthScaleFactor: 20.0, testResults: '**/target/surefire-reports/*.xml'
      step([$class: 'JUnitResultArchiver', testResults: '**/target/surefire-reports/TEST-*.xml'])
    }
    deleteDir()
  }
}