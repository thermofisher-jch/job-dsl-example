node {
  stage('Checkout code') {
    checkout scm
  }
  stage('Compile') {
    sh './gradlew clean :jobs:build'
  }
}
