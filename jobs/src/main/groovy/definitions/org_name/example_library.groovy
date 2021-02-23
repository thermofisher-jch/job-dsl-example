import utilities.DefaultPipeline
import utilities.JobFactory

JobFactory factory = new JobFactory(this, 'org-name', 'library-name', DefaultPipeline.GRADLE)
factory.createFolder()
factory.createMultibranchPipeline()
