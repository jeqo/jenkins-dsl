freeStyleJob('update_fork_RxJava') {
    displayName('update-fork-RxJava')
    description('Rebase the primary branch (1.x) in jeqo/RxJava fork.')

    checkoutRetryCount(3)

    properties {
        githubProjectUrl('https://github.com/jeqo/RxJava')
        sidebarLinks {
            link('https://github.com/ReactiveX/RxJava', 'UPSTREAM: ReactiveX/RxJava', 'notepad.png')
        }
    }

    logRotator {
        numToKeep(2)
        daysToKeep(2)
    }

    scm {
        git {
            remote {
                url('git@github.com:jeqo/RxJava.git')
                name('origin')
                credentials('ssh-github-key')
                refspec('+refs/heads/1.x:refs/remotes/origin/1.x')
            }
            remote {
                url('https://github.com/ReactiveX/RxJava.git')
                name('upstream')
                refspec('+refs/heads/1.x:refs/remotes/upstream/1.x')
            }
            branches('1.x', 'upstream/1.x')
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
        shell('git rebase upstream/1.x')
    }

    publishers {
        postBuildTask {
            git {
                branch('origin', '1.x')
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
