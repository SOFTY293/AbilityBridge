// lib/models/job_listing.dart
import 'dart:convert';

class JobListing {
  final int? jobId;
  final int companyId;
  final String? companyName;
  final String title;
  final String description;
  final String jobType;
  final bool isRemote;
  final Map<String, dynamic>? accommodations;

  JobListing({
    this.jobId,
    required this.companyId,
    this.companyName,
    required this.title,
    required this.description,
    required this.jobType,
    required this.isRemote,
    this.accommodations,
  });

  factory JobListing.fromJson(Map<String, dynamic> json) {
    return JobListing(
      jobId: json['job_id'],
      companyId: json['company_id'],
      companyName: json['company_name'] ?? 'Unknown Company',
      title: json['title'],
      description: json['description'],
      jobType: json['job_type'],
      isRemote: json['is_remote'] == 1 || json['is_remote'] == true,
      accommodations: json['accommodations'] != null
          ? Map<String, dynamic>.from(jsonDecode(json['accommodations'] as String))
          : null,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'company_id': companyId,
      'title': title,
      'description': description,
      'job_type': jobType,
      'is_remote': isRemote,
      'accommodation_offerings': accommodations,
    };
  }
}
