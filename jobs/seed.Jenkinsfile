node {
  stage('Checkout code') {
    checkout scm
  }
  stage('Compile') {
    sh './gradlew clean :jobs:build'
  }
  stage('Seed Jenkins') {
    jobDsl targets: 'jobs/src/main/groovy/definitions/**/*.groovy',
    additionalClasspath: 'jobs/build/libs/*.jar'
  }
}
