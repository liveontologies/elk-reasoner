# Introduction #

The release procedure of ELK is rather complicated since there are lot's of things happening at the same time: cutting off the release in the repository, generation of release notes and readme files, uploading of the distribution files (e.g., binaries), generation and uploading of the reports and javadocs, and releasing artifacts to maven central. Therefore, it is important to follow the steps below to ensure that the release goes as smooth as possible.

# Performing The Release #

  1. Edit the `changes.xml` file located in `elk-distribution/src/changes` and edit the description of the current release (e.g., "First maintenance relase after the third public release"). Make sure that all changes since the last release are described here. Do not replace the version and date as those will be substituted automatically during the release.
  1. Check whether new versions of dependencies are available by running:
```
  mvn versions:display-dependency-updates 
```
> > Unless there are compatibility problems, switch to the latest versions of the dependencies (they can contain important bug fixes). **Do not upgrade the version of log4j (1.2.14) or otherwise the Protege plugin will stop working with Protege 4.1**
  1. Make sure the project compiles fine and all tests work as expected:
```
  mvn clean install
```
  1. Inspect warnings, in particular
```
 [WARNING] Used undeclared dependencies found:
```
> > The dependencies listed in this warning should be included in the pom file of the respecting module.
> > There can also be similar warnings such as:
```
 [WARNING] Unused declared dependencies found:
```
> > This means that the `maven-dependency-plugin` could not see where the specified dependencies were used. You may try to remove the listed dependencies and see if everything still works fine, but the plugin could not always detect all used dependencies.
  1. Check the produced distribution (zip) files in elk-distribution/target:
    1. Unless there have been major changes, they should have similar size as the [files from the previous release](http://code.google.com/p/elk-reasoner/downloads/list)
    1. The Readme.txt and Changes.txt files should have all variables properly expanded. Please, pay attention to the version numbers.
    1. The binaries should work as expected. Make sure to test the ELK Protege plugin with different versions of Protege.
  1. Check the user-local `settings.xml` file located in the `.m2` folder of your home directory. The servers used in pom.xml files and user access credentials should be configured there. Currently the following entries are required:
```
    <server>
      <id>elk-reasoner.googlecode.com</id>
      <username>your-google-id</username>
      <password>your-google-svn-password</password>
    </server>

    <server>
      <id>elk.semanticweb.org</id>
      <username>elkuser</username>
      <privateKey>${user.home}/.ssh/id_rsa</privateKey>
      <passphrase>your-private-key-passphrase</passphrase>
    </server>

    <server>
      <id>sonatype-nexus-snapshots</id>
      <username>your-oss-sonatype-username</username>
      <password>your-oss-sonatype-password</password>
    </server>

    <server>
      <id>sonatype-nexus-staging</id>
      <username>your-oss-sonatype-username</username>
      <password>your-oss-sonatype-password</password>
    </server>
```
  1. Read the [Sonatype OSS Maven Repository Usage Guide](https://docs.sonatype.org/display/Repository/Sonatype+OSS+Maven+Repository+Usage+Guide). In particular, make sure that GPG keys are configured as described in [Section 5. Prerequisites](https://docs.sonatype.org/display/Repository/Sonatype+OSS+Maven+Repository+Usage+Guide#SonatypeOSSMavenRepositoryUsageGuide-5.Prerequisites).
  1. Next step is to test whether the current version satisfies the [Maven Central Requirements](https://docs.sonatype.org/display/Repository/Sonatype+OSS+Maven+Repository+Usage+Guide#SonatypeOSSMavenRepositoryUsageGuide-6.CentralSyncRequirement). Since it is not possible to stage SNAPSHOT versions at the Sonatype repository, we need to temporarily remove the SNAPSHOT suffix from the version numbers of the artifact. For this, run the command:
```
  mvn versions:set
```
> > and enter the version without -SNAPSHOT on the prompt. After this change, issue the following command:
```
  mvn -P release-sign-artifacts deploy
```
> > This will activate the `release-sign-artifacts` profile, which ensures that javadocs are generated, and all artifacts are signed. All artifacts will be deployed to a Sonatype staging repository. After that, log into the [Sonatype staging repositories](https://oss.sonatype.org/index.html#stagingRepositories), select the newly created repository and press `Close` in the menu. This will trigger the Maven Central Requirements tests. Make sure that no error messages appear and the repository status becomes `closed`. After that the repository can be dropped by pressing `Drop` in the menu. Do not worry, it will be generated again during the release phase. Finally, revert the version numbers back to the SNAPSHOT versions by calling the command:
```
  mvn versions:revert
```
  1. Test whether the web site with Maven reports (including javadocs) can be generated and deployed by running:
```
  mvn site site:deploy
```
> > Pay attention to javadoc warnings, e.g., missing linked classes, and fix the issues, if any.
  1. After all tests have been successful, it is time to perform the release:
    1. Set the `MAVEN_OPTS` environment variable to make sure java does not run out of memory during the release:
```
 export MAVEN_OPTS='-Xmx1G -XX:MaxPermSize=128m'
```
    1. Create and switch to a new temporary branch for the release:
```
 git checkout -b release/v[Version]
```
> > > where `[Version]` is the new version number (e.g., 1.2.3)
    1. Prepare for the release:
```
 mvn release:prepare
```
> > > This will ask about the released and next snapshot versions (X.Y.Z, please do not increment Z, it should be used only for back ports) and will create a tag for the release version in the repository (the default values are typically fine). You may want to first run the simulation of this procedure, which does not modify the repository:
```
 mvn release:prepare -D dryRun=true
```
> > > If everything went fine you can run the command without `dryRun` option after performing
```
 mvn release:clean
```
> > > For further information see the [maven-release-plugin documentation](http://maven.apache.org/plugins/maven-release-plugin/).
    1. Perform the release:
```
 mvn release:perform
```
> > > This will check out the created released version from the repository, compile, generate the maven reports website, upload artifacts to a Sonatype staging repository and upload the maven reports website.
> > > If something goes wrong, just delete the created branch `release/v[Version]` and tag `v[Version]` and start over from point 1 above. All changes to the git repository should have been local.
  1. Log into the [Sonatype staging repositories](https://oss.sonatype.org/index.html#stagingRepositories), select the newly created repository and press `Close` in the menu and after that press `Release`. This should activate the Maven Central sync. After a few hours, the new version of ELK should appear at [Maven Central](http://search.maven.org).
  1. The release branch can now be merged back to the original branch (`develop` in case of the major release and `backports` in case of a minor release), and the release branch can be removed
```
  git checkout [original branch]
  git merge --no-ff release/v[Version]  
```

> > In case there is a branch containing all releases (according to the git flow it should be `master`) the version from which released was made (the one but the last version in the release branch) can be merged there:
```
  git checkout master
  git merge --no-ff release/v[Version]~1
```
> > Now the temporary branch can be deleted and all changes can be pushed.
```
  git branch -D release/v[Version]
  git push --all && git push --tags
```
  1. Cut out the release in `elk-distribution/src/changes/changes.xml` by specifying explicitly the latest release version, date and description. They can be found [here](http://elk.semanticweb.org/maven/latest/elk-distribution/changes-report.html). Leave the variables in the new current version. Commit the changes.
  1. Now install the next SNAPSHOT version locally and then run:
```
 mvn clean site source:jar javadoc:jar deploy site:deploy
```
> > This will deploy the the Maven website, sources, and javadoc for the new SNAPSHOT version. Log into the `elk.semanticweb.org`:
```
   ssh elkuser@elk.semanticweb.org
```
> > and change the symbolic links `latest` and `daily` in `/home/elkuser/elk_semanticweb_org/www/maven` to point at the latest released and the latest snapshot folders respectively.
  1. Update the download links on the [main page](https://code.google.com/p/elk-reasoner/) and the [Getting ELK](https://code.google.com/p/elk-reasoner/wiki/GettingElk) page.
  1. Update the information about the new release on the googlecode wiki, [Protege wiki](http://protegewiki.stanford.edu/wiki/ELK), [W3C software wiki](http://www.w3.org/2001/sw/wiki/ELK), and [semanticweb.org wiki](http://semanticweb.org/wiki/ELK)
  1. Post a message at the [ELK user mailing list](https://groups.google.com/forum/#!forum/elk-reasoner-users) about the new release. A draft is generated at `elk-distribution/target/announcement.txt`.
  1. In case of a major release, consider posting announcements at the following mailing lists:  OWL <public-owl-dev@w3.org>, Protege OWL <protege-owl@mailman.stanford.edu>,  OWL API Developer <owlapi-developer@lists.sourceforge.net>,  Description Logic <dl@dl.kr.org>, Semantic Web <semantic-web@w3.org>