ImageJ2 is a new version of ImageJ seeking to strengthen both the software and
its community. Internally, it is a total redesign of ImageJ, but it is
backwards compatible with ImageJ 1.x via a "legacy layer" and features a user
interface closely modeled after the original.

Under the hood, ImageJ2 completely isolates the image processing logic from the
graphical user interface (UI), allowing ImageJ2 commands to be used in many
contexts, including headless in the cloud or on a server such as OMERO, or from
within another application such as KNIME, ICY or CellProfiler (a Python
application).

ImageJ2 has an N-dimensional data model driven by the powerful ImgLib2 library,
which supports image data expressed in an extensible set of numeric and
non-numeric types, and accessed from an extensible set of data sources. ImageJ2
is driven by a state-of-the-art, collaborative development process, including
version control, unit testing, automated builds via a continuous integration
system, a bug tracker and more.

We are collaborating closely with related projects including Fiji, Bio-Formats
and OMERO, and are striving to deliver a coherent software stack reusable
throughout the life sciences community and beyond. For more details, see:
  http://scijava.github.com/

ImageJ2 is currently in the "beta" stage, meaning the code is not finished. It
is being released for early community feedback and testing. Comments, questions
and bug reports are much appreciated!

To maintain ImageJ's continuity of development, we have modeled the application
after ImageJ v1.x as much as is reasonable. However, please be aware that
ImageJ2 is essentially a total rewrite of ImageJ from the ground up. It
provides backward compatibility with older versions of ImageJ by bundling the
latest v1.x code and translating between "legacy" and "modern" image
structures.

For more details on the project, see the ImageJ2 web site at:
  http://developer.imagej.net/


LICENSING
---------

ImageJ2 is distributed under a Simplified BSD License; for the full text of the
license, see the LICENSE.txt file. For further reading, see:
  http://en.wikipedia.org/wiki/BSD_licenses

For the list of developers and contributors, see pom.xml in the source code:
  https://github.com/imagej/imagej/blob/master/pom.xml


BUGS
----

For a list of known issues, see the issue tracking system:
  http://trac.imagej.net/report/1

Please report any bugs by following the instructions at:
  http://developer.imagej.net/reporting-bugs


OTHER NOTES
-----------

Menus: IJ2 is currently made up of both IJ1 & IJ2 commands. IJ1 commands are
distinguished with a small microscope icon next to their name in the menus,
while IJ2 commands have either a green puzzle piece or a custom icon. Many IJ1
commands work, but many do not, for various reasons. We are still working to
improve the number of working IJ1 commands, and/or update them to pure IJ2
commands.

Macros and scripts: It is possible to execute IJ1 macros in IJ2, though you may
experience mixed results. You can even include calls to overridden IJ2 commands
but they will not record correctly.
