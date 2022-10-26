package utilities

import javaposse.jobdsl.dsl.DslFactory
import javaposse.jobdsl.dsl.Folder
import javaposse.jobdsl.dsl.jobs.MultibranchWorkflowJob
import javaposse.jobdsl.dsl.jobs.WorkflowJob

class JobFactory {
  private static final String HOST = 'git.example.com'
  private static final String PIPELINE_ORG = 'Managed-Pipelines'
  private static final String PIPELINE_REPO_NAME = 'Java'
  private static final String PIPELINE_REPO_BRANCH = 'master'
  private static final String PIPELINE_GIT_ORG_URL = "https://$HOST/$PIPELINE_ORG"
  private static final String PIPELINE_GIT_REPO_URL = "$PIPELINE_GIT_ORG_URL/$PIPELINE_REPO_NAME"
  private static final String SCM_CREDENTIALS_ID = '...'
  private static final String BASE_BUILD_PATH = 'Managed-Pipeline/Java'

  static WorkflowJob seedJob(DslFactory factory) {
    factory.pipelineJob("$BASE_BUILD_PATH/_Seed") {
      description 'Pipeline to seed the Managed Pipeline jobs for Java'
      definition {
        cpsScm {
          scm {
            git {
              remote {
                url PIPELINE_GIT_REPO_URL
              }
              branch PIPELINE_REPO_BRANCH
            }
            scriptPath 'jobs/seed.Jenkinsfile'
          }
        }
      }
    }
  }

  static MultibranchWorkflowJob seedJobPrBuilder(DslFactory factory) {
    factory.multibranchPipelineJob("$BASE_BUILD_PATH/_Seed_PR_Builder") {
      branchSources {
        /* for example, when using GitHub */
        github {
          apiUri('...')
          buildForkPRMerge(true)
          buildOriginBranch(true)
          buildOriginBranchWithPR(true)
          buildOriginPRMerge(true)
          checkoutCredentialsId(SCM_CREDENTIALS_ID)
          repoOwner(PIPELINE_ORG)
          repository(PIPELINE_REPO_NAME)
          scanCredentialsId(SCM_CREDENTIALS_ID)
          /* id required to be unique, otherwise triggers won't work across duplicates in your Jenkins instance */
          id("$PIPELINE_ORG-$PIPELINE_REPO_NAME-seed-mb")
        }
      }
      configure {
        def aFactory = it / factory(class: 'com.cloudbees.workflow.multibranch.CustomBranchProjectFactory')
        // aFactory << definition(class:'org.jenkinsci.plugins.workflow.cps.CpsScmFlowDefinition')
        // scm(class: 'hudson.plugins.git.GitSCM') {
        //   userRemoteConfigs {
        //     'hudson.plugins.git.UserRemoteConfig' {
        //       url(PIPELINE_GIT_REPO_URL)
        //     }
        //   }
        //   branches {
        //     'hudson.plugins.git.BranchSpec' {
        //       name(PIPELINE_REPO_BRANCH)
        //     }
        //   }
        // }
        // scriptPath('jobs/buildPR.Jenkinsfile')
      }
    }
  }

  private final DslFactory factory
  private final String gitOrgName
  private final String gitRepoName
  private final DefaultPipeline pipeline
  private final String gitOrgUrl
  private final String gitRepoUrl
  private final String orgBaseBuildPath
  private final String projectBaseBuildPath

  JobFactory(DslFactory factory, String gitOrgName, String gitRepoName, DefaultPipeline pipeline) {
    this.factory = factory
    this.gitOrgName = gitOrgName
    this.gitRepoName = gitRepoName
    this.pipeline = pipeline

    this.gitOrgUrl = "https://$HOST/$gitOrgName"
    this.gitRepoUrl = "${this.gitOrgUrl}/$gitRepoName"
    this.orgBaseBuildPath = "$BASE_BUILD_PATH/$gitOrgName"
    this.projectBaseBuildPath = "$orgBaseBuildPath/$gitRepoName"
  }

  Folder createFolder() {
    factory.folder(orgBaseBuildPath) {
      description("Jobs for building Java projects in the $gitOrgName organisation")
    }
    factory.folder(projectBaseBuildPath) {
      description("Jobs for building the $gitRepoName project")
      properties {
        folderLibraries {
          libraries {
            // allow us to fetch the libraries in `buildutilities`
            libraryConfiguration {
              name 'managed-java-pipeline-library'
              implicit false
              defaultVersion PIPELINE_REPO_BRANCH
              retriever {
                modernSCM {
                  scm {
                    git {
                      remote PIPELINE_GIT_REPO_URL
                      credentialsId SCM_CREDENTIALS_ID
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  MultibranchWorkflowJob createMultibranchPipeline() {
    def jobLocation = "$projectBaseBuildPath/Pipeline"
    def scriptPath = "pipelines/${pipeline.name().toLowerCase()}/multibranch.groovy"

    createMultibranchPipelineDefinition(jobLocation, scriptPath)
  }

  private MultibranchWorkflowJob createMultibranchPipelineDefinition(String jobLocation, String script) {
    factory.multibranchPipelineJob(jobLocation) {
      branchSources {
        /* for example, when using GitHub */
        github {
          apiUri('...')
          buildForkPRMerge(true)
          buildOriginBranch(true)
          buildOriginBranchWithPR(true)
          buildOriginPRMerge(true)
          checkoutCredentialsId(SCM_CREDENTIALS_ID)
          repoOwner(gitOrgName)
          repository(gitRepoName)
          scanCredentialsId(SCM_CREDENTIALS_ID)
          /* id required to be unique, otherwise triggers won't work across duplicates in your Jenkins instance */
          id("$PIPELINE_ORG-$PIPELINE_REPO_NAME-$gitOrgName-$gitRepoName-mb")
        }
      }
      configure {
        def aFactory = it / factory(class: 'com.cloudbees.workflow.multibranch.CustomBranchProjectFactory')
        aFactory << definition(class:'org.jenkinsci.plugins.workflow.cps.CpsScmFlowDefinition')
        scm(class: 'hudson.plugins.git.GitSCM') {
          userRemoteConfigs {
            'hudson.plugins.git.UserRemoteConfig' {
              url(Constants.PIPELINE_GIT_REPO_URL)
            }
          }
          branches {
            'hudson.plugins.git.BranchSpec' {
              name(Constants.PIPELINE_REPO_BRANCH)
            }
          }
        }
        scriptPath(script)
      }
    }
  }
}
