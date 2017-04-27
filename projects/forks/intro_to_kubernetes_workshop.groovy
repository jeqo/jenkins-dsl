freeStyleJob('update_fork_intro_to_kubernetes_workshop') {
    displayName('update-fork-intro-to-kubernetes-workshop')
    description('Rebase the primary branch (master) in jeqo/intro-to-kubernetes-workshop fork.')

    checkoutRetryCount(3)

    properties {
        githubProjectUrl('https://github.com/jeqo/intro-to-kubernetes-workshop')
        sidebarLinks {
            link('https://github.com/kelseyhightower/intro-to-kubernetes-workshop', 'UPSTREAM: kelseyhightower/intro-to-kubernetes-workshop', 'notepad.png')
        }
    }

    logRotator {
        numToKeep(2)
        daysToKeep(2)
    }

    scm {
        git {
            remote {
                url('git@github.com:jeqo/intro-to-kubernetes-workshop.git')
                name('origin')
                credentials('ssh-github-key')
                refspec('+refs/heads/master:refs/remotes/origin/master')
            }
            remote {
                url('https://github.com/kelseyhightower/intro-to-kubernetes-workshop.git')
                name('upstream')
                refspec('+refs/heads/master:refs/remotes/upstream/master')
            }
            branches('master', 'upstream/master')
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
        shell('git rebase upstream/master')
    }

    publishers {
        postBuildTask {
            git {
                branch('origin', 'master')
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
