// lib/models/application.dart
import 'dart:convert';

class Application {
  final int applicationId;
  final int jobId;
  final int seekerId;
  final String seekerName;
  final String jobTitle;
  final String companyName;
  final String? coverLetter;
  final String status;
  final DateTime appliedAt;
  final String? seekerBio;

  Application({
    required this.applicationId,
    required this.jobId,
    required this.seekerId,
    required this.seekerName,
    required this.jobTitle,
    required this.companyName,
    this.coverLetter,
    required this.status,
    required this.appliedAt,
    this.seekerBio,
  });

  factory Application.fromJson(Map<String, dynamic> json) {
    return Application(
      applicationId: json['application_id'],
      jobId: json['job_id'],
      seekerId: json['seeker_id'],
      seekerName: json['seeker_name'] ?? 'Anonymous Candidate',
      jobTitle: json['job_title'],
      companyName: json['company_name'] ?? 'Unknown Company',
      coverLetter: json['cover_letter'],
      status: json['status'],
      appliedAt: DateTime.parse(json['applied_at']),
      seekerBio: json['bio'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'application_id': applicationId,
      'job_id': jobId,
      'seeker_id': seekerId,
      'seeker_name': seekerName,
      'job_title': jobTitle,
      'company_name': companyName,
      'cover_letter': coverLetter,
      'status': status,
      'applied_at': appliedAt.toIso8601String(),
      'bio': seekerBio,
    };
  }
}
