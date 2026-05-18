import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:abilitybridge/core/api/api_client.dart';
import 'package:abilitybridge/core/models/user_model.dart';

class AuthState {
  final UserModel? user;
  final bool isLoading;
  final String? error;
  final bool isAuthenticated;

  const AuthState({
    this.user, this.isLoading = false, this.error, this.isAuthenticated = false,
  });

  AuthState copyWith({
    UserModel? user, bool? isLoading, String? error, bool? isAuthenticated,
  }) => AuthState(
    user: user ?? this.user,
    isLoading: isLoading ?? this.isLoading,
    error: error,
    isAuthenticated: isAuthenticated ?? this.isAuthenticated,
  );
}

class AuthNotifier extends StateNotifier<AuthState> {
  final ApiClient _api;
  AuthNotifier(this._api) : super(const AuthState()) { _check(); }

  Future<void> _check() async {
    if (await TokenStorage.hasToken()) {
      try {
        final res  = await _api.get('/users/me');
        final user = UserModel.fromJson(res.data as Map<String, dynamic>);
        state = state.copyWith(user: user, isAuthenticated: true);
      } catch (_) {
        await TokenStorage.clear();
      }
    }
  }

  Future<bool> register({
    required String? email, required String? phone,
    required String password, required String role,
  }) async {
    state = state.copyWith(isLoading: true, error: null);
    try {
      final res  = await _api.post('/auth/register', data: {
        if (email != null) 'email': email,
        if (phone != null) 'phone': phone,
        'password': password, 'role': role,
      });
      final auth = AuthResponse.fromJson(res.data as Map<String, dynamic>);
      await TokenStorage.save(accessToken: auth.accessToken, refreshToken: auth.refreshToken);
      state = state.copyWith(user: auth.user, isAuthenticated: true, isLoading: false);
      return true;
    } catch (e) {
      state = state.copyWith(isLoading: false, error: _err(e));
      return false;
    }
  }

  Future<bool> login({required String credential, required String password}) async {
    state = state.copyWith(isLoading: true, error: null);
    try {
      final res  = await _api.post('/auth/login',
          data: {'credential': credential, 'password': password});
      final auth = AuthResponse.fromJson(res.data as Map<String, dynamic>);
      await TokenStorage.save(accessToken: auth.accessToken, refreshToken: auth.refreshToken);
      state = state.copyWith(user: auth.user, isAuthenticated: true, isLoading: false);
      return true;
    } catch (e) {
      state = state.copyWith(isLoading: false, error: _err(e));
      return false;
    }
  }

  Future<void> logout() async {
    try {
      final r = await TokenStorage.getRefreshToken();
      if (r != null) await _api.post('/auth/logout', data: {'refreshToken': r});
    } catch (_) {}
    await TokenStorage.clear();
    state = const AuthState();
  }

  String _err(dynamic e) {
    try {
      final d = (e as dynamic).response?.data;
      if (d is Map && d['message'] != null) return d['message'] as String;
    } catch (_) {}
    return 'Something went wrong. Please try again.';
  }
}

final apiClientProvider = Provider<ApiClient>((_) => ApiClient());

final authProvider = StateNotifierProvider<AuthNotifier, AuthState>(
  (ref) => AuthNotifier(ref.watch(apiClientProvider)),
);

final isAuthenticatedProvider = Provider<bool>(
  (ref) => ref.watch(authProvider).isAuthenticated,
);

final currentUserProvider = Provider<UserModel?>(
  (ref) => ref.watch(authProvider).user,
);
