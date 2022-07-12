container("openjdk:17") {
    kotlinScript { api ->
        api.space().projects.automation.deployments.start(
            project = api.projectIdentifier(),
            targetIdentifier = TargetIdentifier.Key("ap-test-builds"),
            version = "0.7-Alpha",
            // automatically update deployment status based on a status of a job
            syncWithAutomationJob = true
        )
    }
}