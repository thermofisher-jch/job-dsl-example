package buildutilities

class GradleBuildFactory implements BuildFactory {

  def jenkins

  GradleBuildFactory(jenkins) {
    this.jenkins = jenkins
  }

  def checkoutScm() {
    execute {
      stage('Checkout') {
        checkout scm
      }
    }
  }

  def clean() {
    execute {
      stage('Clean') {
        sh './gradlew clean'
      }
    }
  }

  def compile() {
    execute {
      stage('Compile') {
        sh './gradlew compileJava'
      }
    }
  }

  def test() {
    execute {
      stage('Test') {
        try {
          sh './gradlew test'
        } finally {
          junit '**/build/test-results/**/*.xml'
        }
      }
    }
  }

  def staticAnalysis() {
    execute {
      stage('Static Analysis') {
        // i.e. sh './gradlew sonarqube'
      }
    }
  }

  def publish() {
    execute {
      if ('master' == env.BRANCH_NAME) {
        stage('Publish') {
          // withCredentials(...)
          sh './gradlew publish'
        }
      }
    }
  }

  def execute(stage) {
    stage.delegate = jenkins
    stage()
  }
}
