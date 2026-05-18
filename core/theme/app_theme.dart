import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';

class AppColors {
  static const primary      = Color(0xFF0A84FF);
  static const primaryDark  = Color(0xFF0060C7);
  static const secondary    = Color(0xFF00C896);
  static const accent       = Color(0xFFFF6B35);
  static const purple       = Color(0xFF7C3AED);
  static const gold         = Color(0xFFFFB800);
  static const bg           = Color(0xFFF8FAFF);
  static const bgDark       = Color(0xFF0D1117);
  static const surface      = Color(0xFFFFFFFF);
  static const surfaceDark  = Color(0xFF161B22);
  static const cardDark     = Color(0xFF21262D);
  static const border       = Color(0xFFE8ECF4);
  static const borderDark   = Color(0xFF30363D);
  static const textPrimary  = Color(0xFF0D1117);
  static const textSecondary= Color(0xFF57606A);
  static const textMuted    = Color(0xFF8B949E);
  static const success      = Color(0xFF2DA44E);
  static const warning      = Color(0xFFD29922);
  static const error        = Color(0xFFCF222E);
  static const seeker       = Color(0xFF0A84FF);
  static const employer     = Color(0xFF7C3AED);
  static const mentor       = Color(0xFFFFB800);
  static const admin        = Color(0xFFCF222E);
}

class AppSpacing {
  static const xs  = 4.0;
  static const sm  = 8.0;
  static const md  = 16.0;
  static const lg  = 24.0;
  static const xl  = 32.0;
  static const xxl = 48.0;
}

class AppRadius {
  static const sm   = 8.0;
  static const md   = 12.0;
  static const lg   = 16.0;
  static const xl   = 24.0;
  static const full = 999.0;
}

class AppTheme {
  static ThemeData get light => ThemeData(
    useMaterial3: true,
    brightness: Brightness.light,
    scaffoldBackgroundColor: AppColors.bg,
    colorScheme: const ColorScheme.light(
      primary: AppColors.primary,
      secondary: AppColors.secondary,
      tertiary: AppColors.accent,
      surface: AppColors.surface,
      error: AppColors.error,
      onPrimary: Colors.white,
      onSecondary: Colors.white,
      onSurface: AppColors.textPrimary,
    ),
    textTheme: _textTheme(AppColors.textPrimary),
    appBarTheme: AppBarTheme(
      backgroundColor: AppColors.surface,
      foregroundColor: AppColors.textPrimary,
      elevation: 0,
      centerTitle: false,
      titleTextStyle: GoogleFonts.plusJakartaSans(
        fontSize: 18, fontWeight: FontWeight.w700,
        color: AppColors.textPrimary, letterSpacing: -0.3,
      ),
    ),
    cardTheme: CardThemeData(
      color: AppColors.surface,
      elevation: 0,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(16),
        side: const BorderSide(color: AppColors.border),
      ),
      margin: EdgeInsets.zero,
    ),
    elevatedButtonTheme: ElevatedButtonThemeData(
      style: ElevatedButton.styleFrom(
        backgroundColor: AppColors.primary,
        foregroundColor: Colors.white,
        elevation: 0,
        padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 14),
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
        textStyle: GoogleFonts.plusJakartaSans(
            fontSize: 15, fontWeight: FontWeight.w600),
      ),
    ),
    outlinedButtonTheme: OutlinedButtonThemeData(
      style: OutlinedButton.styleFrom(
        foregroundColor: AppColors.primary,
        side: const BorderSide(color: AppColors.primary, width: 1.5),
        padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 14),
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
        textStyle: GoogleFonts.plusJakartaSans(
            fontSize: 15, fontWeight: FontWeight.w600),
      ),
    ),
    inputDecorationTheme: InputDecorationTheme(
      filled: true,
      fillColor: AppColors.bg,
      border: OutlineInputBorder(
        borderRadius: BorderRadius.circular(12),
        borderSide: const BorderSide(color: AppColors.border),
      ),
      enabledBorder: OutlineInputBorder(
        borderRadius: BorderRadius.circular(12),
        borderSide: const BorderSide(color: AppColors.border),
      ),
      focusedBorder: OutlineInputBorder(
        borderRadius: BorderRadius.circular(12),
        borderSide: const BorderSide(color: AppColors.primary, width: 2),
      ),
      errorBorder: OutlineInputBorder(
        borderRadius: BorderRadius.circular(12),
        borderSide: const BorderSide(color: AppColors.error),
      ),
      contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 14),
      labelStyle: GoogleFonts.plusJakartaSans(
          color: AppColors.textSecondary, fontSize: 14),
      hintStyle: GoogleFonts.plusJakartaSans(
          color: AppColors.textMuted, fontSize: 14),
    ),
    dividerTheme: const DividerThemeData(color: AppColors.border, thickness: 1),
  );

  static ThemeData get dark => ThemeData(
    useMaterial3: true,
    brightness: Brightness.dark,
    scaffoldBackgroundColor: AppColors.bgDark,
    colorScheme: const ColorScheme.dark(
      primary: AppColors.primary,
      secondary: AppColors.secondary,
      surface: AppColors.surfaceDark,
      error: AppColors.error,
    ),
    textTheme: _textTheme(Colors.white),
    appBarTheme: AppBarTheme(
      backgroundColor: AppColors.surfaceDark,
      foregroundColor: Colors.white,
      elevation: 0,
      titleTextStyle: GoogleFonts.plusJakartaSans(
        fontSize: 18, fontWeight: FontWeight.w700, color: Colors.white),
    ),
    cardTheme: CardThemeData(
      color: AppColors.cardDark,
      elevation: 0,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(16),
        side: const BorderSide(color: AppColors.borderDark),
      ),
      margin: EdgeInsets.zero,
    ),
    inputDecorationTheme: InputDecorationTheme(
      filled: true,
      fillColor: AppColors.surfaceDark,
      border: OutlineInputBorder(
        borderRadius: BorderRadius.circular(12),
        borderSide: const BorderSide(color: AppColors.borderDark),
      ),
      enabledBorder: OutlineInputBorder(
        borderRadius: BorderRadius.circular(12),
        borderSide: const BorderSide(color: AppColors.borderDark),
      ),
      focusedBorder: OutlineInputBorder(
        borderRadius: BorderRadius.circular(12),
        borderSide: const BorderSide(color: AppColors.primary, width: 2),
      ),
      contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 14),
      labelStyle: GoogleFonts.plusJakartaSans(color: AppColors.textMuted, fontSize: 14),
      hintStyle: GoogleFonts.plusJakartaSans(color: AppColors.textMuted, fontSize: 14),
    ),
  );

  static TextTheme _textTheme(Color c) => TextTheme(
    displayLarge:  GoogleFonts.plusJakartaSans(fontSize: 57, fontWeight: FontWeight.w800, color: c, letterSpacing: -1.5),
    displayMedium: GoogleFonts.plusJakartaSans(fontSize: 45, fontWeight: FontWeight.w700, color: c),
    displaySmall:  GoogleFonts.plusJakartaSans(fontSize: 36, fontWeight: FontWeight.w700, color: c),
    headlineLarge: GoogleFonts.plusJakartaSans(fontSize: 32, fontWeight: FontWeight.w700, color: c, letterSpacing: -0.3),
    headlineMedium:GoogleFonts.plusJakartaSans(fontSize: 24, fontWeight: FontWeight.w700, color: c),
    headlineSmall: GoogleFonts.plusJakartaSans(fontSize: 20, fontWeight: FontWeight.w600, color: c),
    titleLarge:    GoogleFonts.plusJakartaSans(fontSize: 18, fontWeight: FontWeight.w600, color: c),
    titleMedium:   GoogleFonts.plusJakartaSans(fontSize: 16, fontWeight: FontWeight.w600, color: c),
    titleSmall:    GoogleFonts.plusJakartaSans(fontSize: 14, fontWeight: FontWeight.w600, color: c),
    bodyLarge:     GoogleFonts.plusJakartaSans(fontSize: 16, fontWeight: FontWeight.w400, color: c),
    bodyMedium:    GoogleFonts.plusJakartaSans(fontSize: 14, fontWeight: FontWeight.w400, color: c),
    bodySmall:     GoogleFonts.plusJakartaSans(fontSize: 12, fontWeight: FontWeight.w400, color: c),
    labelLarge:    GoogleFonts.plusJakartaSans(fontSize: 14, fontWeight: FontWeight.w600, color: c),
    labelMedium:   GoogleFonts.plusJakartaSans(fontSize: 12, fontWeight: FontWeight.w500, color: c),
    labelSmall:    GoogleFonts.plusJakartaSans(fontSize: 11, fontWeight: FontWeight.w500, color: c),
  );
}
