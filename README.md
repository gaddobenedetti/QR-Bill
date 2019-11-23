# QRBill - Swiss Payments Code Serializer

This <a href="https://github.com/gaddobenedetti/QR-Bill/blob/master/src/com/gfb/test/QRBill.java">QRBill class</a> was designed to be a bare bones serializer for the Swiss Payments Code, which is meant to adapt the payment system in Switzerland and Liechtenstein to the international ISO 20022 standard. For more information on this please consult the <a href="http://www.paymentstandards.ch/">PaymentStandards.ch</a> site, as the <a href="https://www.paymentstandards.ch/dam/downloads/ig-qr-bill-en.pdf">implementation guide</a> here was the basis for this class.

To note on this implementation is that the above guide contained an example which includes a number of errors in the alternative schema portion of the code. As a result the Main test class will fail on initial validation due to this error and a second Correct class was created to fix it and as a demonstration of how a QR Bill object may be edited.

This class was purposely written using only core Java and without any imports, so that it can more easily adapted to other languages and platforms. It is designed, not only to serialize, but also to be editable and to validate data according to the above standard.

As such it does not implement <i>java.io.Serializable</i>, as is, but the implementation cab be simply added to the class.

This software is released under the <a href="LICENSE.md">MIT Licence</a>.

<h2>Test Project</h2>

The test project uses ZXing to scan and generate QR codes - specifically the core and javase packages and runs four tests. Instructions on how to import ZXing may be found at their <a href="https://github.com/zxing/zxing">Git repository</a> although the test project is already set up to include them.

There are two principle test classes for testing three and four, version 1.0 and 2.0, example QR codes receptively. Each QR image is read, serialized and then recreated from the serialized data. Finally, this copy is read and checked against the original scan. Additionally, to the example scans, the QRBill class is use to generate a QR image from scratch, which is then read to check against expected data. All image examples come from the implementation guide.

It should be noted that the <a href="./qr_tests/Rechnung1.png">first version 1.0 image</a> will not fully scan. This is because the optional fields for alternative schema given in the example do not follow the described format as laid out in the implementation guide, despite, ironically, coming from the same guide. As a result they are omitted during serialization and do no appear in subsequent versions.

The test project consists of three classes:
<ul>
  <li> <b>Test_QRBill_v1.java</b> The main (version 1.0) test class to be run.</li>
  <li> <b>Correct_QRBill_v1.java</b> A second (version 1.0) test class, which reads in the example that contains an error, edits and corrects it before carrying out the remainder of the tests as in the Main class.</li>
  <li> <b>Test_QRBill_v3.java</b> The main (version 2.0) test class to be run.</li>
  <li> <b>ZXing.java</b> A series of static helper methods, associated with interfacing with ZXing.</li>
  <li> <b>QRBillHelper.java</b> A series of static miscellaneous helper methods.</li>
  <li> <b>QRBill.java</b> The <b>Swiss Payments Code Serializer</b> class.</li>
</ul>

<h3>Dependancies</h3>

For the test project, the <b>core</b> and <b>javase</b> ZXing libraries will need to be imported. Additionally the following directories of note and files are also included:
<ul>
  <li> <b>qr_tests_v1</b> A directory with three test QR version 1.0 test images, taken from the implementation guide. Note that the first actually contains an error in the alternative schema portion of the code.</li>
  <li> <b>qr_tests_v2</b> A directory with three test QR version 2.0 test images, taken from the implementation guide. Note that the first actually contains an error in the alternative schema portion of the code.</li>
  <li> <b>qrbill_kreuz.png</b> A logo that is embedded in the QR bill and is required by the implementation guide. It can be found in the res directory.</li>
</ul>

<!--h2>Other Implementations</h2>

A number of other implantations of the Swiss Payments Code Serializer are or are planned. These are:
<ul>
  <li> <b>Java</b>. Please note that due to the vanilla nature of the class, it is compatible with other Java versions and implementations, such as Android.</li>
  <li> <b>C#</b>. Planned - not yet available.</li>
  <li> <b>C++</b>. Planned - not yet available.</li>
  <li> <b>PHP</b>. Planned - not yet available.</li>
  <li> <b>Python</b>. Planned - not yet available.</li>
  <li> <b>Dart</b>. Planned - not yet available.</li>
</ul-->
