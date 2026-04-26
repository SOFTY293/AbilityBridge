import 'package:flutter/material.dart';

// BASE CLASS (Inheritance)
abstract class AbilityScreen extends StatelessWidget {
  final String title;
  const AbilityScreen(this.title, {super.key});

  Widget buildBody(BuildContext context);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(title),
        backgroundColor: Colors.blueAccent,
        foregroundColor: Colors.white,
        // ADD THESE ACTIONS:
        actions: [
          IconButton(
            icon: const Icon(Icons.report_gmailerrorred),
            onPressed: () => Navigator.pushNamed(context, '/report'),
          ),
          IconButton(
            icon: const Icon(Icons.account_circle),
            onPressed: () => Navigator.pushNamed(context, '/profile'),
          ),
        ],
      ),
      body: SafeArea(
        child: Padding(
          padding: const EdgeInsets.all(16.0),
          child: buildBody(context),
        ),
      ),
    );
  }
}

// SHARED WIDGET (Polymorphism)
class CustomButton extends StatelessWidget {
  final String label;
  final VoidCallback onTap;
  final bool primary;

  const CustomButton({
    required this.label,
    required this.onTap,
    this.primary = true,
    super.key,
  });

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 8.0),
      child: ElevatedButton(
        style: ElevatedButton.styleFrom(
          backgroundColor: primary ? Colors.blueAccent : Colors.grey[200],
          foregroundColor: primary ? Colors.white : Colors.black,
          minimumSize: const Size(double.infinity, 55),
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(12),
          ),
        ),
        onPressed: onTap,
        child: Text(
          label,
          style: const TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
        ),
      ),
    );
  }
}
