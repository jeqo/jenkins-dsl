freeStyleJob('update_fork_camunda_bpm_spring_boot_starter') {
    displayName('update-fork-camunda-bpm-spring-boot-starter')
    description('Rebase the primary branch (master) in jeqo/camunda-bpm-spring-boot-starter fork.')

    checkoutRetryCount(3)

    properties {
        githubProjectUrl('https://github.com/jeqo/camunda-bpm-spring-boot-starter')
        sidebarLinks {
            link('https://github.com/camunda/camunda-bpm-spring-boot-starter', 'UPSTREAM: camunda/camunda-bpm-spring-boot-starter', 'notepad.png')
        }
    }

    logRotator {
        numToKeep(2)
        daysToKeep(2)
    }

    scm {
        git {
            remote {
                url('git@github.com:jeqo/camunda-bpm-spring-boot-starter.git')
                name('origin')
                credentials('ssh-github-key')
                refspec('+refs/heads/master:refs/remotes/origin/master')
            }
            remote {
                url('https://github.com/camunda/camunda-bpm-spring-boot-starter.git')
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
        postBuildScripts {
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
