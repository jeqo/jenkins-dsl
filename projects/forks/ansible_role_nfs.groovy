freeStyleJob('update_fork_ansible_role_nfs') {
    displayName('update-fork-ansible-role-nfs')
    description('Rebase the primary branch (master) in jeqo/ansible-role-nfs fork.')

    checkoutRetryCount(3)

    properties {
        githubProjectUrl('https://github.com/jeqo/ansible-role-nfs')
        sidebarLinks {
            link('https://github.com/geerlingguy/ansible-role-nfs', 'UPSTREAM: geerlingguy/ansible-role-nfs', 'notepad.png')
        }
    }

    logRotator {
        numToKeep(2)
        daysToKeep(2)
    }

    scm {
        git {
            remote {
                url('git@github.com:jeqo/ansible-role-nfs.git')
                name('origin')
                credentials('ssh-github-key')
                refspec('+refs/heads/master:refs/remotes/origin/master')
            }
            remote {
                url('https://github.com/geerlingguy/ansible-role-nfs.git')
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
