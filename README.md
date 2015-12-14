# Traceability Assistant
[![Build Status](https://secure.travis-ci.org/germanattanasio/traceability-assistant.svg)](http://travis-ci.org/germanattanasio/traceability-assistant)
[![Coverage Status](https://coveralls.io/repos/germanattanasio/traceability-assistant/badge.svg)](https://coveralls.io/github/germanattanasio/traceability-assistant)

TRAS(Traceability Assistant) is a semi-automated tool that help analysts uncover traceability links between requirements documents described as use cases and architectural documents.

The project is distributed as a jar file. See the [release](https://github.com/germanattanasio/traceability-assistant/releases/latest) section.

If you are interested in the eclipse plugins go to: http://traceability-assistant.mybluemix.net/

## How do I get set up? ###

### Assumptions

 * You have eclipse installed. See https://eclipse.org/downloads/
 * You have maven installed. See https://maven.apache.org/
 * You have UIMA RUTA installed. Update site: http://www.apache.org/dist/uima/eclipse-update-site

### Steps

1. Download the code:

        git clone git@github.com:germanattanasio/traceability-assistant.git

1. Install the [SVD](https://github.com/lucasmaystre/svdlibc) library.
2. Open a terminal and type `svd`.
3. If you get `command not found`, export the library folder to your PATH.

        export PATH=$PATH:/path/to/svdlibc/directory

    e.g: `/Users/foo/svdlibc` that way the runtime can find the svd command.

    If you are using eclipse you need to look at the environment. You may need to add the path variable in  your run configuration and include the path to `svd`as showed above.
    You can also follow the instructions in this [blog](http://architectryan.com/2012/10/02/add-to-the-path-on-mac-os-x-mountain-lion/#.UtSw2vbVVyo)

4. Open a terminal and type:

        mvn clean compile eclipse:eclipse

5. Open eclipse, import the project and refresh the workspace.



## License

  This sample code is licensed under Apache 2.0. Full license text is available in [LICENSE](LICENSE).  
  This sample code is using REAssistant which is licensed under [Eclipse Public License - v 1.0](http://www.eclipse.org/legal/epl-v10.html).

## Contributing

  See [CONTRIBUTING](CONTRIBUTING.md).
