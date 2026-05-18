import 'package:dio/dio.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:logger/logger.dart';

// ── Base URL — change this to match your setup ───────────────
// Chrome (web browser on same PC as backend): localhost
// Android emulator: 10.0.2.2
// Real phone on same WiFi: your PC's local IP e.g. 192.168.1.X
const _baseUrl = 'http://localhost:8080/api/v1';

const _storage = FlutterSecureStorage();
final _log     = Logger(printer: PrettyPrinter(methodCount: 0));

class ApiClient {
  static final ApiClient _instance = ApiClient._internal();
  factory ApiClient() => _instance;
  late final Dio dio;

  ApiClient._internal() {
    dio = Dio(BaseOptions(
      baseUrl: _baseUrl,
      connectTimeout: const Duration(seconds: 15),
      receiveTimeout: const Duration(seconds: 30),
      headers: {
        'Content-Type': 'application/json',
        'Accept':       'application/json',
      },
    ));

    dio.interceptors.add(InterceptorsWrapper(
      onRequest: (options, handler) async {
        final token = await _storage.read(key: 'access_token');
        if (token != null) {
          options.headers['Authorization'] = 'Bearer $token';
        }
        _log.d('→ ${options.method} ${options.path}');
        handler.next(options);
      },
      onError: (error, handler) async {
        _log.e('✗ ${error.response?.statusCode} ${error.requestOptions.path}');
        if (error.response?.statusCode == 401) {
          try {
            final refresh = await _storage.read(key: 'refresh_token');
            if (refresh != null) {
              final res = await Dio().post(
                '$_baseUrl/auth/refresh',
                data: {'refreshToken': refresh},
              );
              final newToken = res.data['accessToken'] as String;
              await _storage.write(key: 'access_token', value: newToken);
              error.requestOptions.headers['Authorization'] = 'Bearer $newToken';
              final retried = await dio.request<dynamic>(
                error.requestOptions.path,
                options: Options(
                  method:  error.requestOptions.method,
                  headers: error.requestOptions.headers,
                ),
                data:            error.requestOptions.data,
                queryParameters: error.requestOptions.queryParameters,
              );
              return handler.resolve(retried);
            }
          } catch (_) {
            await _storage.deleteAll();
          }
        }
        handler.next(error);
      },
      onResponse: (response, handler) {
        _log.d('✓ ${response.statusCode} ${response.requestOptions.path}');
        handler.next(response);
      },
    ));
  }

  Future<Response<dynamic>> get(String path, {Map<String, dynamic>? params}) =>
      dio.get(path, queryParameters: params);

  Future<Response<dynamic>> post(String path, {dynamic data}) =>
      dio.post(path, data: data);

  Future<Response<dynamic>> put(String path, {dynamic data}) =>
      dio.put(path, data: data);

  Future<Response<dynamic>> patch(String path, {dynamic data}) =>
      dio.patch(path, data: data);

  Future<Response<dynamic>> delete(String path) =>
      dio.delete(path);
}

class TokenStorage {
  static Future<void> save({
    required String accessToken,
    required String refreshToken,
  }) async {
    await _storage.write(key: 'access_token',  value: accessToken);
    await _storage.write(key: 'refresh_token', value: refreshToken);
  }

  static Future<String?> getAccessToken()  => _storage.read(key: 'access_token');
  static Future<String?> getRefreshToken() => _storage.read(key: 'refresh_token');
  static Future<void>    clear()           => _storage.deleteAll();

  static Future<bool> hasToken() async =>
      (await _storage.read(key: 'access_token')) != null;
}
