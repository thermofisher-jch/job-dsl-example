@Library('managed-java-pipeline-library')
import buildutilities.BuildFactory
import buildutilities.GradleBuildFactory

BuildFactory factory = new GradleBuildFactory(this)

node {
  docker.image('openjdk:8-alpine').inside('') {
    factory.checkoutScm()
    factory.clean()
    factory.compile()
    factory.test()
    factory.staticAnalysis()
    factory.publish()
  }
}
