freeStyleJob('update_fork_ansible') {
    displayName('update-fork-ansible')
    description('Rebase the primary branch (devel) in jeqo/ansible fork.')

    checkoutRetryCount(3)

    properties {
        githubProjectUrl('https://github.com/jeqo/ansible')
        sidebarLinks {
            link('https://github.com/ansible/ansible', 'UPSTREAM: ansible/ansible', 'notepad.png')
        }
    }

    logRotator {
        numToKeep(2)
        daysToKeep(2)
    }

    scm {
        git {
            remote {
                url('git@github.com:jeqo/ansible.git')
                name('origin')
                credentials('ssh-github-key')
                refspec('+refs/heads/devel:refs/remotes/origin/devel')
            }
            remote {
                url('https://github.com/ansible/ansible.git')
                name('upstream')
                refspec('+refs/heads/devel:refs/remotes/upstream/devel')
            }
            branches('devel', 'upstream/devel')
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
        shell('git rebase upstream/devel')
    }

    publishers {
        postBuildTask {
            git {
                branch('origin', 'devel')
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
