class JobListing {
  final String id;
  final String? employerId;
  final String? companyName;
  final String? companyLogoUrl;
  final String title;
  final String description;
  final String jobType;
  final String? location;
  final bool isRemote;
  final double? salaryMin;
  final double? salaryMax;
  final String currency;
  final String status;
  final String? applicationDeadline;
  final String? accommodationsOffered;
  final double? matchScore;
  final List<String> requiredSkills;
  final String? createdAt;

  const JobListing({
    required this.id,
    this.employerId,
    this.companyName,
    this.companyLogoUrl,
    required this.title,
    required this.description,
    required this.jobType,
    this.location,
    this.isRemote = false,
    this.salaryMin,
    this.salaryMax,
    this.currency = 'USD',
    required this.status,
    this.applicationDeadline,
    this.accommodationsOffered,
    this.matchScore,
    this.requiredSkills = const [],
    this.createdAt,
  });

  factory JobListing.fromJson(Map<String, dynamic> json) => JobListing(
    id:                    json['id'] as String,
    employerId:            json['employerId'] as String?,
    companyName:           json['companyName'] as String?,
    companyLogoUrl:        json['companyLogoUrl'] as String?,
    title:                 json['title'] as String,
    description:           json['description'] as String,
    jobType:               json['jobType'] as String? ?? 'FULL_TIME',
    location:              json['location'] as String?,
    isRemote:              json['isRemote'] as bool? ?? false,
    salaryMin:             (json['salaryMin'] as num?)?.toDouble(),
    salaryMax:             (json['salaryMax'] as num?)?.toDouble(),
    currency:              json['currency'] as String? ?? 'USD',
    status:                json['status'] as String? ?? 'ACTIVE',
    applicationDeadline:   json['applicationDeadline'] as String?,
    accommodationsOffered: json['accommodationsOffered'] as String?,
    matchScore:            (json['matchScore'] as num?)?.toDouble(),
    requiredSkills:        (json['requiredSkills'] as List<dynamic>?)
        ?.map((e) => e as String).toList() ?? [],
    createdAt:             json['createdAt'] as String?,
  );

  String get salaryRange {
    if (salaryMin == null && salaryMax == null) return 'Not specified';
    final min = salaryMin != null ? '\$${salaryMin!.toInt()}k' : '';
    final max = salaryMax != null ? '\$${salaryMax!.toInt()}k' : '';
    if (min.isEmpty) return 'Up to $max';
    if (max.isEmpty) return 'From $min';
    return '$min – $max';
  }

  String get jobTypeLabel {
    switch (jobType) {
      case 'FULL_TIME':  return 'Full-time';
      case 'PART_TIME':  return 'Part-time';
      case 'CONTRACT':   return 'Contract';
      case 'INTERNSHIP': return 'Internship';
      case 'VOLUNTEER':  return 'Volunteer';
      default:           return jobType;
    }
  }
}

class MicroTask {
  final String id;
  final String? posterId;
  final String? posterName;
  final String title;
  final String description;
  final String? category;
  final double payRate;
  final String currency;
  final String? requirements;
  final String? deadline;
  final String status;
  final String? createdAt;

  const MicroTask({
    required this.id,
    this.posterId,
    this.posterName,
    required this.title,
    required this.description,
    this.category,
    required this.payRate,
    this.currency = 'USD',
    this.requirements,
    this.deadline,
    required this.status,
    this.createdAt,
  });

  factory MicroTask.fromJson(Map<String, dynamic> json) => MicroTask(
    id:           json['id'] as String,
    posterId:     json['posterId'] as String?,
    posterName:   json['posterName'] as String?,
    title:        json['title'] as String,
    description:  json['description'] as String,
    category:     json['category'] as String?,
    payRate:      (json['payRate'] as num?)?.toDouble() ?? 0,
    currency:     json['currency'] as String? ?? 'USD',
    requirements: json['requirements'] as String?,
    deadline:     json['deadline'] as String?,
    status:       json['status'] as String? ?? 'OPEN',
    createdAt:    json['createdAt'] as String?,
  );
}

// Renamed from Notification → AppNotification to avoid clash with Flutter's built-in Notification class
class AppNotification {
  final String id;
  final String type;
  final String title;
  final String? body;
  final bool isRead;
  final String? createdAt;

  const AppNotification({
    required this.id,
    required this.type,
    required this.title,
    this.body,
    this.isRead = false,
    this.createdAt,
  });

  factory AppNotification.fromJson(Map<String, dynamic> json) => AppNotification(
    id:        json['id'] as String,
    type:      json['type'] as String? ?? '',
    title:     json['title'] as String? ?? '',
    body:      json['body'] as String?,
    isRead:    json['isRead'] as bool? ?? false,
    createdAt: json['createdAt'] as String?,
  );
}
