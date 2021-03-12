import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:permission_handler/permission_handler.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  static const platform = MethodChannel("save");
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        body: Container(
          child: Center(
            child: TextButton(
              child: const Text("Click Me"),
              onPressed: () async {
                if (await Permission.storage.request().isGranted) {
                  final ByteData data = await rootBundle.load("img/test.jpg");
                  await platform.invokeMethod("saveAndOpenImage",
                      {"data": data.buffer.asUint8List(), "name": "name"});
                }
                print(
                    "Nope hhdty gfktuf gutv rrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrrr");
              },
            ),
          ),
        ),
      ),
    );
  }
}
