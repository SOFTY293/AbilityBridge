import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';

import 'package:abilitybridge/core/providers/auth_provider.dart';
import 'package:abilitybridge/core/models/user_model.dart';
import 'package:abilitybridge/features/onboarding/onboarding_screen.dart';
import 'package:abilitybridge/features/auth/login_screen.dart';
import 'package:abilitybridge/features/auth/register_screen.dart';
import 'package:abilitybridge/features/home/home_screen.dart';
import 'package:abilitybridge/features/jobs/job_list_screen.dart';
import 'package:abilitybridge/features/jobs/job_detail_screen.dart';
import 'package:abilitybridge/features/jobs/job_apply_screen.dart';
import 'package:abilitybridge/features/tasks/task_list_screen.dart';
import 'package:abilitybridge/features/tasks/task_detail_screen.dart';
import 'package:abilitybridge/features/skills/skills_screen.dart';
import 'package:abilitybridge/features/accommodation/accommodation_screen.dart';
import 'package:abilitybridge/features/employer/employer_dashboard_screen.dart';
import 'package:abilitybridge/features/mentorship/mentorship_screen.dart';
import 'package:abilitybridge/features/messaging/conversations_screen.dart';
import 'package:abilitybridge/features/messaging/chat_screen.dart';
import 'package:abilitybridge/features/notifications/notifications_screen.dart';
import 'package:abilitybridge/features/profile/profile_screen.dart';
import 'package:abilitybridge/features/admin/admin_screen.dart';
import 'package:abilitybridge/core/theme/app_theme.dart';
import 'package:abilitybridge/core/widgets/shared_widgets.dart';

final routerProvider = Provider<GoRouter>((ref) {
  final authState = ref.watch(authProvider);

  return GoRouter(
    initialLocation: '/onboarding',
    redirect: (context, state) {
      final isAuth = authState.isAuthenticated;
      final isAuthRoute = state.matchedLocation.startsWith('/login') ||
          state.matchedLocation.startsWith('/register') ||
          state.matchedLocation.startsWith('/onboarding');

      if (!isAuth && !isAuthRoute) return '/login';
      if (isAuth && isAuthRoute) return '/home';
      return null;
    },
    routes: [
      GoRoute(path: '/onboarding', builder: (_, __) => const OnboardingScreen()),
      GoRoute(path: '/login',      builder: (_, __) => const LoginScreen()),
      GoRoute(path: '/register',   builder: (_, __) => const RegisterScreen()),

      ShellRoute(
        builder: (context, state, child) => MainShell(child: child),
        routes: [
          GoRoute(path: '/home',          builder: (_, __) => const HomeScreen()),
          GoRoute(path: '/profile',       builder: (_, __) => const ProfileScreen()),
          GoRoute(path: '/notifications', builder: (_, __) => const NotificationsScreen()),
          GoRoute(path: '/jobs',          builder: (_, __) => const JobListScreen()),
          GoRoute(
            path: '/jobs/:id',
            builder: (_, state) =>
                JobDetailScreen(jobId: state.pathParameters['id']!),
          ),
          GoRoute(
            path: '/jobs/:id/apply',
            builder: (_, state) =>
                JobApplyScreen(jobId: state.pathParameters['id']!),
          ),
          GoRoute(path: '/tasks', builder: (_, __) => const TaskListScreen()),
          GoRoute(
            path: '/tasks/:id',
            builder: (_, state) =>
                TaskDetailScreen(taskId: state.pathParameters['id']!),
          ),
          GoRoute(path: '/skills',        builder: (_, __) => const SkillsScreen()),
          GoRoute(path: '/accommodation', builder: (_, __) => const AccommodationScreen()),
          GoRoute(
            path: '/employer/:id',
            builder: (_, state) =>
                EmployerDashboardScreen(employerId: state.pathParameters['id']!),
          ),
          GoRoute(path: '/mentorship', builder: (_, __) => const MentorshipScreen()),
          GoRoute(path: '/messages',   builder: (_, __) => const ConversationsScreen()),
          GoRoute(
            path: '/messages/:conversationId',
            builder: (_, state) =>
                ChatScreen(conversationId: state.pathParameters['conversationId']!),
          ),
          GoRoute(path: '/admin', builder: (_, __) => const AdminScreen()),
        ],
      ),
    ],
    errorBuilder: (context, state) => Scaffold(
      body: Center(child: Text('Page not found: ${state.uri}')),
    ),
  );
});

// ── Bottom Navigation Shell ───────────────────────────────────
class MainShell extends ConsumerWidget {
  final Widget child;
  const MainShell({super.key, required this.child});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final user = ref.watch(currentUserProvider);
    final loc  = GoRouterState.of(context).uri.toString();

    int currentIndex = 0;
    if (loc.startsWith('/jobs'))     currentIndex = 1;
    if (loc.startsWith('/tasks'))    currentIndex = 2;
    if (loc.startsWith('/messages')) currentIndex = 3;
    if (loc.startsWith('/profile'))  currentIndex = 4;

    return Scaffold(
      body: child,
      bottomNavigationBar: NavigationBar(
        selectedIndex: currentIndex,
        onDestinationSelected: (i) {
          switch (i) {
            case 0: GoRouter.of(context).go('/home');     break;
            case 1: GoRouter.of(context).go('/jobs');     break;
            case 2: GoRouter.of(context).go('/tasks');    break;
            case 3: GoRouter.of(context).go('/messages'); break;
            case 4: GoRouter.of(context).go('/profile');  break;
          }
        },
        destinations: const [
          NavigationDestination(
            icon: Icon(Icons.home_outlined),
            selectedIcon: Icon(Icons.home),
            label: 'Home',
          ),
          NavigationDestination(
            icon: Icon(Icons.work_outline),
            selectedIcon: Icon(Icons.work),
            label: 'Jobs',
          ),
          NavigationDestination(
            icon: Icon(Icons.bolt_outlined),
            selectedIcon: Icon(Icons.bolt),
            label: 'Tasks',
          ),
          NavigationDestination(
            icon: Icon(Icons.chat_bubble_outline),
            selectedIcon: Icon(Icons.chat_bubble),
            label: 'Messages',
          ),
          NavigationDestination(
            icon: Icon(Icons.person_outline),
            selectedIcon: Icon(Icons.person),
            label: 'Profile',
          ),
        ],
      ),
    );
  }
}
