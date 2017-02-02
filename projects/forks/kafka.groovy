freeStyleJob('update_fork_kafka') {
    displayName('update-fork-kafka')
    description('Rebase the primary branch (trunk) in jeqo/kafka fork.')

    checkoutRetryCount(3)

    properties {
        githubProjectUrl('https://github.com/jeqo/kafka')
        sidebarLinks {
            link('https://github.com/apache/kafka', 'UPSTREAM: apache/kafka', 'notepad.png')
        }
    }

    logRotator {
        numToKeep(2)
        daysToKeep(2)
    }

    scm {
        git {
            remote {
                url('git@github.com:jeqo/kafka.git')
                name('origin')
                credentials('ssh-github-key')
                refspec('+refs/heads/trunk:refs/remotes/origin/trunk')
            }
            remote {
                url('https://github.com/apache/kafka.git')
                name('upstream')
                refspec('+refs/heads/trunk:refs/remotes/upstream/trunk')
            }
            branches('trunk', 'upstream/trunk')
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
        shell('git rebase upstream/trunk')
    }

    publishers {
        postBuildScripts {
            git {
                branch('origin', 'trunk')
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
