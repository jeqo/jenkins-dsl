freeStyleJob('update_fork_packer_qemu_templates') {
    displayName('update-fork-packer-qemu-templates')
    description('Rebase the primary branch (master) in jeqo/packer-qemu-templates fork.')

    checkoutRetryCount(3)

    properties {
        githubProjectUrl('https://github.com/jeqo/packer-qemu-templates')
        sidebarLinks {
            link('https://github.com/jakobadam/packer-qemu-templates', 'UPSTREAM: jakobadam/packer-qemu-templates', 'notepad.png')
        }
    }

    logRotator {
        numToKeep(2)
        daysToKeep(2)
    }

    scm {
        git {
            remote {
                url('git@github.com:jeqo/packer-qemu-templates.git')
                name('origin')
                credentials('ssh-github-key')
                refspec('+refs/heads/master:refs/remotes/origin/master')
            }
            remote {
                url('https://github.com/jakobadam/packer-qemu-templates.git')
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
