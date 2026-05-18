import 'package:flutter/material.dart';
import 'package:flutter_animate/flutter_animate.dart';
import 'package:percent_indicator/percent_indicator.dart';
import 'package:abilitybridge/core/theme/app_theme.dart';

class AppButton extends StatelessWidget {
  final String label;
  final VoidCallback? onPressed;
  final bool isLoading;
  final bool outlined;
  final Color? color;
  final IconData? icon;
  final double? width;

  const AppButton({
    super.key, required this.label, this.onPressed,
    this.isLoading = false, this.outlined = false,
    this.color, this.icon, this.width,
  });

  @override
  Widget build(BuildContext context) {
    Widget child = isLoading
        ? const SizedBox(width: 20, height: 20,
            child: CircularProgressIndicator(strokeWidth: 2, color: Colors.white))
        : Row(mainAxisSize: MainAxisSize.min, children: [
            if (icon != null) ...[Icon(icon, size: 18), const SizedBox(width: 8)],
            Text(label),
          ]);

    final btn = outlined
        ? OutlinedButton(onPressed: isLoading ? null : onPressed, child: child)
        : ElevatedButton(
            style: ElevatedButton.styleFrom(
                backgroundColor: color ?? AppColors.primary),
            onPressed: isLoading ? null : onPressed, child: child);

    return width != null ? SizedBox(width: width, child: btn) : btn;
  }
}

class AppTextField extends StatelessWidget {
  final String label;
  final String? hint;
  final TextEditingController? controller;
  final bool obscureText;
  final TextInputType keyboardType;
  final String? Function(String?)? validator;
  final Widget? prefixIcon;
  final Widget? suffixIcon;
  final int maxLines;
  final void Function(String)? onChanged;

  const AppTextField({
    super.key, required this.label, this.hint, this.controller,
    this.obscureText = false, this.keyboardType = TextInputType.text,
    this.validator, this.prefixIcon, this.suffixIcon,
    this.maxLines = 1, this.onChanged,
  });

  @override
  Widget build(BuildContext context) => TextFormField(
    controller: controller, obscureText: obscureText,
    keyboardType: keyboardType, validator: validator,
    maxLines: maxLines, onChanged: onChanged,
    decoration: InputDecoration(
      labelText: label, hintText: hint,
      prefixIcon: prefixIcon, suffixIcon: suffixIcon,
    ),
  );
}

class AppCard extends StatelessWidget {
  final Widget child;
  final EdgeInsetsGeometry? padding;
  final VoidCallback? onTap;
  final Color? color;

  const AppCard({super.key, required this.child, this.padding, this.onTap, this.color});

  @override
  Widget build(BuildContext context) => Card(
    color: color,
    child: InkWell(
      onTap: onTap,
      borderRadius: BorderRadius.circular(AppRadius.lg),
      child: Padding(
        padding: padding ?? const EdgeInsets.all(AppSpacing.md),
        child: child,
      ),
    ),
  );
}

class AppLoading extends StatelessWidget {
  final String? message;
  const AppLoading({super.key, this.message});

  @override
  Widget build(BuildContext context) => Center(
    child: Column(mainAxisSize: MainAxisSize.min, children: [
      const CircularProgressIndicator(),
      if (message != null) ...[
        const SizedBox(height: AppSpacing.md),
        Text(message!, style: Theme.of(context).textTheme.bodyMedium),
      ],
    ]),
  );
}

class AppError extends StatelessWidget {
  final String message;
  final VoidCallback? onRetry;
  const AppError({super.key, required this.message, this.onRetry});

  @override
  Widget build(BuildContext context) => Center(
    child: Padding(
      padding: const EdgeInsets.all(AppSpacing.xl),
      child: Column(mainAxisSize: MainAxisSize.min, children: [
        const Icon(Icons.error_outline, size: 48, color: AppColors.error),
        const SizedBox(height: AppSpacing.md),
        Text(message, textAlign: TextAlign.center,
            style: Theme.of(context).textTheme.bodyMedium),
        if (onRetry != null) ...[
          const SizedBox(height: AppSpacing.md),
          AppButton(label: 'Retry', onPressed: onRetry),
        ],
      ]),
    ),
  );
}

class MatchScoreBadge extends StatelessWidget {
  final double score;
  const MatchScoreBadge({super.key, required this.score});

  Color get _color {
    if (score >= 80) return AppColors.success;
    if (score >= 50) return AppColors.warning;
    return AppColors.error;
  }

  @override
  Widget build(BuildContext context) => CircularPercentIndicator(
    radius: 28, lineWidth: 4,
    percent: (score / 100).clamp(0, 1),
    center: Text('${score.toInt()}%',
        style: const TextStyle(fontSize: 10, fontWeight: FontWeight.w700)),
    progressColor: _color,
    backgroundColor: _color.withOpacity(0.15),
    circularStrokeCap: CircularStrokeCap.round,
  );
}

class RoleBadge extends StatelessWidget {
  final String role;
  const RoleBadge({super.key, required this.role});

  Color get _color {
    switch (role.toUpperCase()) {
      case 'SEEKER':   return AppColors.seeker;
      case 'EMPLOYER': return AppColors.employer;
      case 'MENTOR':   return AppColors.mentor;
      case 'ADMIN':    return AppColors.admin;
      default:         return AppColors.textMuted;
    }
  }
  String get _label {
    switch (role.toUpperCase()) {
      case 'SEEKER':   return 'Job Seeker';
      case 'EMPLOYER': return 'Employer';
      case 'MENTOR':   return 'Mentor';
      case 'ADMIN':    return 'Admin';
      default:         return role;
    }
  }

  @override
  Widget build(BuildContext context) => Container(
    padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
    decoration: BoxDecoration(
      color: _color.withOpacity(0.12),
      borderRadius: BorderRadius.circular(AppRadius.full),
      border: Border.all(color: _color.withOpacity(0.3)),
    ),
    child: Text(_label,
        style: TextStyle(color: _color, fontSize: 11, fontWeight: FontWeight.w600)),
  );
}

class AppEmptyState extends StatelessWidget {
  final IconData icon;
  final String title;
  final String? subtitle;
  final String? actionLabel;
  final VoidCallback? onAction;

  const AppEmptyState({
    super.key, required this.icon, required this.title,
    this.subtitle, this.actionLabel, this.onAction,
  });

  @override
  Widget build(BuildContext context) => Center(
    child: Padding(
      padding: const EdgeInsets.all(AppSpacing.xl),
      child: Column(mainAxisSize: MainAxisSize.min, children: [
        Container(
          width: 80, height: 80,
          decoration: BoxDecoration(
            color: AppColors.primary.withOpacity(0.1), shape: BoxShape.circle),
          child: Icon(icon, size: 36, color: AppColors.primary),
        ).animate().scale(duration: const Duration(milliseconds: 400),
            curve: Curves.elasticOut),
        const SizedBox(height: AppSpacing.lg),
        Text(title, style: Theme.of(context).textTheme.titleMedium,
            textAlign: TextAlign.center)
            .animate().fadeIn(delay: const Duration(milliseconds: 200)),
        if (subtitle != null) ...[
          const SizedBox(height: AppSpacing.sm),
          Text(subtitle!,
              style: Theme.of(context).textTheme.bodyMedium
                  ?.copyWith(color: AppColors.textSecondary),
              textAlign: TextAlign.center)
              .animate().fadeIn(delay: const Duration(milliseconds: 300)),
        ],
        if (actionLabel != null && onAction != null) ...[
          const SizedBox(height: AppSpacing.lg),
          AppButton(label: actionLabel!, onPressed: onAction)
              .animate().fadeIn(delay: const Duration(milliseconds: 400))
              .slideY(begin: 0.3),
        ],
      ]),
    ),
  );
}

class SectionHeader extends StatelessWidget {
  final String title;
  final String? action;
  final VoidCallback? onAction;

  const SectionHeader({super.key, required this.title, this.action, this.onAction});

  @override
  Widget build(BuildContext context) => Row(
    mainAxisAlignment: MainAxisAlignment.spaceBetween,
    children: [
      Text(title, style: Theme.of(context).textTheme.titleMedium),
      if (action != null)
        TextButton(
          onPressed: onAction,
          child: Text(action!,
              style: const TextStyle(color: AppColors.primary, fontSize: 13)),
        ),
    ],
  );
}
