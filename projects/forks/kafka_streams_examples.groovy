freeStyleJob('update_fork_kafka_streams_examples') {
    displayName('update-fork-kafka-streams-examples')
    description('Rebase the primary branch (5.0.0-post) in jeqo/kafka-streams-examples fork.')

    checkoutRetryCount(3)

    properties {
        githubProjectUrl('https://github.com/jeqo/kafka-streams-examples')
        sidebarLinks {
            link('https://github.com/confluentinc/kafka-streams-examples', 'UPSTREAM: confluentinc/kafka-streams-examples', 'notepad.png')
        }
    }

    logRotator {
        numToKeep(2)
        daysToKeep(2)
    }

    scm {
        git {
            remote {
                url('git@github.com:jeqo/kafka-streams-examples.git')
                name('origin')
                credentials('ssh-github-key')
                refspec('+refs/heads/5.0.0-post:refs/remotes/origin/5.0.0-post')
            }
            remote {
                url('https://github.com/confluentinc/kafka-streams-examples.git')
                name('upstream')
                refspec('+refs/heads/5.0.0-post:refs/remotes/upstream/5.0.0-post')
            }
            branches('5.0.0-post', 'upstream/5.0.0-post')
            extensions {
                disableRemotePoll()
                wipeOutWorkspace()
                cleanAfterCheckout()
            }
        }
    }

    triggers {
        cron('H H * * *')
    }

    wrappers { colorizeOutput() }

    steps {
        shell('git rebase upstream/5.0.0-post')
    }

    publishers {
        postBuildTask {
            git {
                branch('origin', '5.0.0-post')
                pushOnlyIfSuccess()
            }
        }

        extendedEmail {
            recipientList('$DEFAULT_RECIPIENTS')
            contentType('text/plain')
            triggers {
                stillFailing {
                    attachBuildLog(true)
                }
            }
        }

        wsCleanup()
    }
}
