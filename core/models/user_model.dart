import 'package:equatable/equatable.dart';

enum UserRole { seeker, employer, mentor, admin }
enum AccountStatus { active, pendingVerification, suspended, removed }

class UserModel extends Equatable {
  final String id;
  final String? email;
  final String? phone;
  final UserRole role;
  final AccountStatus status;
  final bool emailVerified;
  final DateTime? createdAt;

  const UserModel({
    required this.id, this.email, this.phone,
    required this.role, required this.status,
    required this.emailVerified, this.createdAt,
  });

  factory UserModel.fromJson(Map<String, dynamic> j) => UserModel(
    id:           j['id'] as String,
    email:        j['email'] as String?,
    phone:        j['phone'] as String?,
    role:         _role(j['role'] as String? ?? 'SEEKER'),
    status:       _status(j['status'] as String? ?? 'ACTIVE'),
    emailVerified:j['emailVerified'] as bool? ?? false,
    createdAt:    j['createdAt'] != null
        ? DateTime.tryParse(j['createdAt'] as String) : null,
  );

  static UserRole _role(String s) {
    switch (s.toUpperCase()) {
      case 'EMPLOYER': return UserRole.employer;
      case 'MENTOR':   return UserRole.mentor;
      case 'ADMIN':    return UserRole.admin;
      default:         return UserRole.seeker;
    }
  }

  static AccountStatus _status(String s) {
    switch (s.toUpperCase().replaceAll('_', '')) {
      case 'SUSPENDED':          return AccountStatus.suspended;
      case 'REMOVED':            return AccountStatus.removed;
      case 'PENDINGVERIFICATION':return AccountStatus.pendingVerification;
      default:                   return AccountStatus.active;
    }
  }

  String get displayName => email ?? phone ?? 'User';

  String get roleLabel {
    switch (role) {
      case UserRole.employer: return 'Employer';
      case UserRole.mentor:   return 'Mentor';
      case UserRole.admin:    return 'Admin';
      default:                return 'Job Seeker';
    }
  }

  @override
  List<Object?> get props => [id, email, phone, role, status];
}

class AuthResponse {
  final String accessToken;
  final String refreshToken;
  final UserModel user;

  const AuthResponse({required this.accessToken, required this.refreshToken, required this.user});

  factory AuthResponse.fromJson(Map<String, dynamic> j) => AuthResponse(
    accessToken:  j['accessToken']  as String,
    refreshToken: j['refreshToken'] as String,
    user: UserModel.fromJson(j['user'] as Map<String, dynamic>),
  );
}

class SeekerProfile {
  final String? id;
  final String fullName;
  final String? headline;
  final String? bio;
  final String? location;
  final String? profilePictureUrl;
  final String? cvUrl;
  final bool anonymousMode;
  final String? targetJobCategory;
  final String? availability;

  const SeekerProfile({
    this.id, required this.fullName, this.headline, this.bio,
    this.location, this.profilePictureUrl, this.cvUrl,
    this.anonymousMode = false, this.targetJobCategory, this.availability,
  });

  factory SeekerProfile.fromJson(Map<String, dynamic> j) => SeekerProfile(
    id: j['id'] as String?,
    fullName: j['fullName'] as String? ?? '',
    headline: j['headline'] as String?,
    bio: j['bio'] as String?,
    location: j['location'] as String?,
    profilePictureUrl: j['profilePictureUrl'] as String?,
    cvUrl: j['cvUrl'] as String?,
    anonymousMode: j['anonymousMode'] as bool? ?? false,
    targetJobCategory: j['targetJobCategory'] as String?,
    availability: j['availability'] as String?,
  );

  Map<String, dynamic> toJson() => {
    'fullName': fullName, 'headline': headline, 'bio': bio,
    'location': location, 'targetJobCategory': targetJobCategory,
    'availability': availability,
  };
}

class EmployerProfile {
  final String? id;
  final String companyName;
  final String? companyLogoUrl;
  final String? industry;
  final String? companySize;
  final String? website;
  final String? description;
  final String? location;
  final bool isVerified;

  const EmployerProfile({
    this.id, required this.companyName, this.companyLogoUrl, this.industry,
    this.companySize, this.website, this.description, this.location,
    this.isVerified = false,
  });

  factory EmployerProfile.fromJson(Map<String, dynamic> j) => EmployerProfile(
    id: j['id'] as String?,
    companyName: j['companyName'] as String? ?? '',
    companyLogoUrl: j['companyLogoUrl'] as String?,
    industry: j['industry'] as String?,
    companySize: j['companySize'] as String?,
    website: j['website'] as String?,
    description: j['description'] as String?,
    location: j['location'] as String?,
    isVerified: j['isVerified'] as bool? ?? false,
  );
}
