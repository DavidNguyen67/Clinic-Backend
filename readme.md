# API Reference

Tài liệu tổng hợp đầy đủ các endpoint, request DTO và response DTO của toàn bộ API.

---

## Quy ước chung

- Base path trong bảng là path đầy đủ của API.
- Các API `POST` và `PATCH` nhận JSON body theo nghiệp vụ tương ứng.
- Các API dạng danh sách `GET /` có thể nhận query params để lọc/phân trang.
- `{id}`, `{appointmentId}` là path parameter.
- Prefix `/admin`, `/doctor`, `/staff`, `/patient` yêu cầu quyền tương ứng. Prefix `/public` là public.

### Response Convention

| API type              | Response                    |
| --------------------- | --------------------------- |
| `GET /` list          | `ApiPaged<T>`               |
| `GET /count`          | `Long`                      |
| `GET /{id}` detail    | `T` hoặc response DTO riêng |
| `POST /` create       | `T` hoặc response DTO riêng |
| `PATCH /{id}` update  | `T` hoặc response DTO riêng |
| `PATCH /restore/{id}` | `T`                         |
| `DELETE /{id}`        | `204 No Content`            |

### Error Response

Tất cả lỗi trả về format chung: `RestErrorDto`

---

## Auth

| Method | Path                       | Mô tả                                   | Request DTO                | Response                                               |
| ------ | -------------------------- | --------------------------------------- | -------------------------- | ------------------------------------------------------ |
| POST   | `/v1/auth/register`        | Đăng ký tài khoản                       | `RegisterRequestDTO`       | `{ user: UserResponseDTO, accessToken, refreshToken }` |
| POST   | `/v1/auth/login`           | Đăng nhập                               | `LoginRequestDTO`          | `{ user: UserResponseDTO, accessToken, refreshToken }` |
| POST   | `/v1/auth/refresh`         | Làm mới token                           | `RefreshRequestDTO`        | `{ user: UserResponseDTO, accessToken, refreshToken }` |
| POST   | `/v1/auth/logout`          | Đăng xuất                               | `LogoutRequestDTO`         | `204 No Content`                                       |
| POST   | `/v1/auth/forgot-password` | Gửi yêu cầu quên mật khẩu              | `ForgotPasswordRequestDTO` | `String`                                               |
| POST   | `/v1/auth/verify-otp`      | Xác thực OTP                            | `VerifyOtpRequestDTO`      | `{ resetToken }`                                       |
| POST   | `/v1/auth/reset-password`  | Đặt lại mật khẩu                        | `ResetPasswordRequestDTO`  | `204 No Content`                                       |
| POST   | `/v1/auth/change-password` | Đổi mật khẩu                            | `ChangePasswordRequestDTO` | `204 No Content`                                       |
| GET    | `/v1/auth/profile`         | Lấy profile người dùng đang đăng nhập   | None                       | `UserProfileDTO`                                       |
| GET    | `/v1/auth/count-email`     | Kiểm tra/đếm email                      | None (query email)         | Count/check result                                     |
| PATCH  | `/v1/auth/update-profile`  | Cập nhật profile người dùng đang đăng nhập | `UpdateProfileDto`      | `User`                                                 |

---

## Appointment

### Doctor Appointment

| Method | Path                                  | Mô tả                             | Request DTO             | Response                         |
| ------ | ------------------------------------- | --------------------------------- | ----------------------- | -------------------------------- |
| POST   | `/v1/doctor/appointment/`             | Tạo lịch hẹn bởi doctor           | `CreateAppointmentDto`  | `ResponseAppointmentDto`         |
| GET    | `/v1/doctor/appointment/`             | Lấy danh sách lịch hẹn của doctor | Query lọc/phân trang    | `ApiPaged<ResponseAppointmentDto>` |
| GET    | `/v1/doctor/appointment/count`        | Đếm lịch hẹn của doctor           | Query lọc               | `Long`                           |
| GET    | `/v1/doctor/appointment/statistics`   | Thống kê lịch hẹn của doctor      | Query thời gian/lọc     | `AppointmentStatisticsDto`       |
| GET    | `/v1/doctor/appointment/{id}`         | Lấy chi tiết lịch hẹn             | `id`                    | `ResponseAppointmentDto`         |
| PATCH  | `/v1/doctor/appointment/{id}`         | Cập nhật lịch hẹn                 | `UpdateAppointmentDto`  | `ResponseAppointmentDto`         |
| PATCH  | `/v1/doctor/appointment/restore/{id}` | Khôi phục lịch hẹn đã xóa         | `id`                    | `ResponseAppointmentDto`         |
| DELETE | `/v1/doctor/appointment/{id}`         | Xóa lịch hẹn                      | `id`                    | `204 No Content`                 |

### Staff Appointment

| Method | Path                               | Mô tả                            | Request DTO            | Response                           |
| ------ | ---------------------------------- | -------------------------------- | ---------------------- | ---------------------------------- |
| GET    | `/v1/staff/appointment/`           | Lấy danh sách lịch hẹn cho staff | Query lọc/phân trang   | `ApiPaged<ResponseAppointmentDto>` |
| GET    | `/v1/staff/appointment/statistics` | Thống kê lịch hẹn cho staff      | Query thời gian/lọc    | `AppointmentStatisticsDto`         |
| GET    | `/v1/staff/appointment/{id}`       | Lấy chi tiết lịch hẹn            | `id`                   | `ResponseAppointmentDto`           |
| PATCH  | `/v1/staff/appointment/{id}`       | Cập nhật lịch hẹn                | `UpdateAppointmentDto` | `ResponseAppointmentDto`           |

### Patient Appointment

| Method | Path                                 | Mô tả                                | Request DTO            | Response                           |
| ------ | ------------------------------------ | ------------------------------------ | ---------------------- | ---------------------------------- |
| POST   | `/v1/patient/appointment/`           | Bệnh nhân tạo lịch hẹn               | `CreateAppointmentDto` | `ResponseAppointmentDto`           |
| GET    | `/v1/patient/appointment/`           | Lấy danh sách lịch hẹn của bệnh nhân | Query lọc/phân trang   | `ApiPaged<ResponseAppointmentDto>` |
| GET    | `/v1/patient/appointment/statistics` | Thống kê lịch hẹn của bệnh nhân      | Query thời gian/lọc    | `AppointmentStatisticsDto`         |
| GET    | `/v1/patient/appointment/next`       | Lấy lịch hẹn sắp tới                 | Auth token             | `ResponseAppointmentDto`           |
| GET    | `/v1/patient/appointment/{id}`       | Lấy chi tiết lịch hẹn                | `id`                   | `ResponseAppointmentDto`           |
| PATCH  | `/v1/patient/appointment/{id}`       | Cập nhật lịch hẹn                    | `UpdateAppointmentDto` | `ResponseAppointmentDto`           |

### Public Appointment

| Method | Path                      | Mô tả                         | Request DTO          | Response                           |
| ------ | ------------------------- | ----------------------------- | -------------------- | ---------------------------------- |
| GET    | `/v1/public/appointment/` | Lấy danh sách lịch hẹn public | Query lọc/phân trang | `ApiPaged<ResponseAppointmentDto>` |

---

## Conversation

### Patient Conversation

| Method | Path                                    | Mô tả                         | Request DTO               | Response                            |
| ------ | --------------------------------------- | ----------------------------- | ------------------------- | ----------------------------------- |
| POST   | `/v1/patient/conversation/`             | Tạo cuộc trò chuyện           | `CreateConversationDto`   | `ResponseConversationDto`           |
| GET    | `/v1/patient/conversation/`             | Lấy danh sách cuộc trò chuyện | Query lọc/phân trang      | `ApiPaged<ResponseConversationDto>` |
| GET    | `/v1/patient/conversation/count`        | Đếm cuộc trò chuyện           | Query lọc                 | `Long`                              |
| GET    | `/v1/patient/conversation/{id}`         | Lấy chi tiết cuộc trò chuyện  | `id`                      | `ResponseConversationDto`           |
| PATCH  | `/v1/patient/conversation/{id}`         | Cập nhật cuộc trò chuyện      | `UpdateConversationDto`   | `ResponseConversationDto`           |
| PATCH  | `/v1/patient/conversation/restore/{id}` | Khôi phục cuộc trò chuyện     | `id`                      | `ResponseConversationDto`           |
| DELETE | `/v1/patient/conversation/{id}`         | Xóa cuộc trò chuyện           | `id`                      | `204 No Content`                    |

### Doctor Conversation

| Method | Path                                   | Mô tả                         | Request DTO             | Response                            |
| ------ | -------------------------------------- | ----------------------------- | ----------------------- | ----------------------------------- |
| POST   | `/v1/doctor/conversation/`             | Tạo cuộc trò chuyện           | `CreateConversationDto` | `ResponseConversationDto`           |
| GET    | `/v1/doctor/conversation/`             | Lấy danh sách cuộc trò chuyện | Query lọc/phân trang    | `ApiPaged<ResponseConversationDto>` |
| GET    | `/v1/doctor/conversation/count`        | Đếm cuộc trò chuyện           | Query lọc               | `Long`                              |
| GET    | `/v1/doctor/conversation/{id}`         | Lấy chi tiết cuộc trò chuyện  | `id`                    | `ResponseConversationDto`           |
| PATCH  | `/v1/doctor/conversation/{id}`         | Cập nhật cuộc trò chuyện      | `UpdateConversationDto` | `ResponseConversationDto`           |
| PATCH  | `/v1/doctor/conversation/restore/{id}` | Khôi phục cuộc trò chuyện     | `id`                    | `ResponseConversationDto`           |
| DELETE | `/v1/doctor/conversation/{id}`         | Xóa cuộc trò chuyện           | `id`                    | `204 No Content`                    |

---

## Doctor Profile

| Method | Path                                    | Mô tả                              | Request DTO               | Response                                                                    |
| ------ | --------------------------------------- | ---------------------------------- | ------------------------- | --------------------------------------------------------------------------- |
| POST   | `/v1/admin/doctor-profile/`             | Admin tạo hồ sơ bác sĩ             | `CreateDoctorProfileDto`  | `DoctorProfile`                                                             |
| POST   | `/v1/admin/doctor-profile/seed`         | Seed dữ liệu hồ sơ bác sĩ          | —                         | —                                                                           |
| GET    | `/v1/admin/doctor-profile/`             | Admin lấy danh sách hồ sơ bác sĩ   | Query lọc/phân trang      | `ApiPaged<ResponseDoctorProfileDto>`                                        |
| GET    | `/v1/admin/doctor-profile/count`        | Admin đếm hồ sơ bác sĩ             | Query lọc                 | `Long`                                                                      |
| GET    | `/v1/admin/doctor-profile/{id}`         | Admin lấy chi tiết hồ sơ bác sĩ    | `id`                      | `ResponseDoctorProfileDetailDto`                                            |
| PATCH  | `/v1/admin/doctor-profile/{id}`         | Admin cập nhật hồ sơ bác sĩ        | `UpdateDoctorProfileDto`  | `DoctorProfile`                                                             |
| PATCH  | `/v1/admin/doctor-profile/restore/{id}` | Admin khôi phục hồ sơ bác sĩ       | `id`                      | `DoctorProfile`                                                             |
| DELETE | `/v1/admin/doctor-profile/{id}`         | Admin xóa hồ sơ bác sĩ             | `id`                      | `204 No Content`                                                            |
| POST   | `/v1/doctor/doctor-profile/`            | Doctor tạo hồ sơ của mình          | `CreateDoctorProfileDto`  | `DoctorProfile`                                                             |
| PATCH  | `/v1/doctor/doctor-profile/{id}`        | Doctor cập nhật hồ sơ của mình     | `UpdateDoctorProfileDto`  | `DoctorProfile`                                                             |
| GET    | `/v1/public/doctor-profile/`            | Public lấy danh sách hồ sơ bác sĩ  | Query lọc/phân trang      | `ApiPaged<ResponseDoctorProfileDto>`                                        |
| GET    | `/v1/public/doctor-profile/count`       | Public đếm hồ sơ bác sĩ            | Query lọc                 | `Long`                                                                      |
| GET    | `/v1/public/doctor-profile/{id}`        | Public lấy chi tiết hồ sơ bác sĩ   | `id`                      | `ResponseDoctorProfileDetailDto`                                            |

---

## Doctor Schedule Exception

| Method | Path                                               | Mô tả                              | Request DTO                              | Response                             |
| ------ | -------------------------------------------------- | ---------------------------------- | ---------------------------------------- | ------------------------------------ |
| POST   | `/v1/admin/doctor-schedule-exception/`             | Tạo ngoại lệ lịch làm việc bác sĩ  | `CreateDoctorScheduleExceptionDto`       | `DoctorScheduleException`            |
| POST   | `/v1/admin/doctor-schedule-exception/bulk-create`  | Tạo nhiều ngoại lệ lịch làm việc   | `List<CreateDoctorScheduleExceptionDto>` | `List<DoctorScheduleException>`      |
| GET    | `/v1/admin/doctor-schedule-exception/`             | Admin lấy danh sách ngoại lệ lịch  | Query lọc/phân trang                     | `ApiPaged<DoctorScheduleException>`  |
| GET    | `/v1/admin/doctor-schedule-exception/count`        | Admin đếm ngoại lệ lịch            | Query lọc                                | `Long`                               |
| GET    | `/v1/admin/doctor-schedule-exception/{id}`         | Admin lấy chi tiết ngoại lệ lịch   | `id`                                     | `DoctorScheduleException`            |
| PATCH  | `/v1/admin/doctor-schedule-exception/{id}`         | Admin cập nhật ngoại lệ lịch       | `UpdateDoctorScheduleExceptionDto`       | `DoctorScheduleException`            |
| PATCH  | `/v1/admin/doctor-schedule-exception/restore/{id}` | Admin khôi phục ngoại lệ lịch      | `id`                                     | `DoctorScheduleException`            |
| DELETE | `/v1/admin/doctor-schedule-exception/{id}`         | Admin xóa ngoại lệ lịch            | `id`                                     | `204 No Content`                     |
| GET    | `/v1/public/doctor-schedule-exception/`            | Public lấy danh sách ngoại lệ lịch | Query lọc/phân trang                     | `ApiPaged<DoctorScheduleException>`  |
| GET    | `/v1/public/doctor-schedule-exception/count`       | Public đếm ngoại lệ lịch           | Query lọc                                | `Long`                               |
| GET    | `/v1/public/doctor-schedule-exception/{id}`        | Public lấy chi tiết ngoại lệ lịch  | `id`                                     | `DoctorScheduleException`            |

---

## Staff Profile

| Method | Path                                   | Mô tả                               | Request DTO              | Response                  |
| ------ | -------------------------------------- | ----------------------------------- | ------------------------ | ------------------------- |
| POST   | `/v1/admin/staff-profile/`             | Admin tạo hồ sơ nhân viên           | `CreateStaffProfileDto`  | `StaffProfile`            |
| GET    | `/v1/admin/staff-profile/`             | Admin lấy danh sách hồ sơ nhân viên | Query lọc/phân trang     | `ApiPaged<StaffProfile>`  |
| GET    | `/v1/admin/staff-profile/count`        | Admin đếm hồ sơ nhân viên           | Query lọc                | `Long`                    |
| GET    | `/v1/admin/staff-profile/{id}`         | Admin lấy chi tiết hồ sơ nhân viên  | `id`                     | `StaffProfile`            |
| PATCH  | `/v1/admin/staff-profile/{id}`         | Admin cập nhật hồ sơ nhân viên      | `UpdateStaffProfileDto`  | `StaffProfile`            |
| PATCH  | `/v1/admin/staff-profile/restore/{id}` | Admin khôi phục hồ sơ nhân viên     | `id`                     | `StaffProfile`            |
| DELETE | `/v1/admin/staff-profile/{id}`         | Admin xóa hồ sơ nhân viên           | `id`                     | `204 No Content`          |
| POST   | `/v1/staff/staff-profile/`             | Staff tạo hồ sơ của mình            | `CreateStaffProfileDto`  | `StaffProfile`            |
| PATCH  | `/v1/staff/staff-profile/{id}`         | Staff cập nhật hồ sơ của mình       | `UpdateStaffProfileDto`  | `StaffProfile`            |

---

## Patient Profile

| Method | Path                                     | Mô tả                               | Request DTO                | Response                    |
| ------ | ---------------------------------------- | ----------------------------------- | -------------------------- | --------------------------- |
| POST   | `/v1/admin/patient-profile/`             | Admin tạo hồ sơ bệnh nhân           | `CreatePatientProfileDto`  | `PatientProfile`            |
| GET    | `/v1/admin/patient-profile/`             | Admin lấy danh sách hồ sơ bệnh nhân | Query lọc/phân trang       | `ApiPaged<PatientProfile>`  |
| GET    | `/v1/admin/patient-profile/count`        | Admin đếm hồ sơ bệnh nhân           | Query lọc                  | `Long`                      |
| GET    | `/v1/admin/patient-profile/{id}`         | Admin lấy chi tiết hồ sơ bệnh nhân  | `id`                       | `PatientProfile`            |
| PATCH  | `/v1/admin/patient-profile/{id}`         | Admin cập nhật hồ sơ bệnh nhân      | `UpdatePatientProfileDto`  | `PatientProfile`            |
| PATCH  | `/v1/admin/patient-profile/restore/{id}` | Admin khôi phục hồ sơ bệnh nhân     | `id`                       | `PatientProfile`            |
| DELETE | `/v1/admin/patient-profile/{id}`         | Admin xóa hồ sơ bệnh nhân           | `id`                       | `204 No Content`            |
| POST   | `/v1/public/patient-profile/`            | Public tạo hồ sơ bệnh nhân          | `CreatePatientProfileDto`  | `PatientProfile`            |
| GET    | `/v1/public/patient-profile/count`       | Public đếm hồ sơ bệnh nhân          | Query lọc                  | `Long`                      |
| POST   | `/v1/patient/patient-profile/`           | Patient tạo hồ sơ của mình          | `CreatePatientProfileDto`  | `PatientProfile`            |
| PATCH  | `/v1/patient/patient-profile/{id}`       | Patient cập nhật hồ sơ của mình     | `UpdatePatientProfileDto`  | `PatientProfile`            |

---

## Specialty

| Method | Path                                     | Mô tả                                | Request DTO           | Response                                                         |
| ------ | ---------------------------------------- | ------------------------------------ | --------------------- | ---------------------------------------------------------------- |
| POST   | `/v1/admin/specialty/`                   | Admin tạo chuyên khoa                | `CreateSpecialtyDto`  | `Specialty`                                                      |
| POST   | `/v1/admin/specialty/seed`               | Seed dữ liệu chuyên khoa             | —                     | —                                                                |
| GET    | `/v1/admin/specialty/`                   | Admin lấy danh sách chuyên khoa      | Query lọc/phân trang  | `ApiPaged<Specialty>`                                            |
| GET    | `/v1/admin/specialty/count`              | Admin đếm chuyên khoa                | Query lọc             | `Long`                                                           |
| GET    | `/v1/admin/specialty/{id}`               | Admin lấy chi tiết chuyên khoa       | `id`                  | `Specialty`                                                      |
| PATCH  | `/v1/admin/specialty/{id}`               | Admin cập nhật chuyên khoa           | `UpdateSpecialtyDto`  | `Specialty`                                                      |
| PATCH  | `/v1/admin/specialty/restore/{id}`       | Admin khôi phục chuyên khoa          | `id`                  | `Specialty`                                                      |
| DELETE | `/v1/admin/specialty/{id}`               | Admin xóa chuyên khoa                | `id`                  | `204 No Content`                                                 |
| GET    | `/v1/public/specialty/`                  | Public lấy danh sách chuyên khoa     | Query lọc/phân trang  | `ApiPaged<Specialty>`                                            |
| GET    | `/v1/public/specialty/with-doctor-count` | Public lấy chuyên khoa kèm số bác sĩ | Query lọc/phân trang  | `ApiPaged<SpecialtyWithDoctorCountDto>`                          |
| GET    | `/v1/public/specialty/count`             | Public đếm chuyên khoa               | Query lọc             | `Long`                                                           |
| GET    | `/v1/public/specialty/statistics`        | Public lấy thống kê chuyên khoa      | Query thời gian/lọc   | `SpecialtyStatisticsDto`                                         |
| GET    | `/v1/public/specialty/{id}`              | Public lấy chi tiết chuyên khoa      | `id`                  | `Specialty`                                                      |

---

## Service

| Method | Path                             | Mô tả                        | Request DTO          | Response                    |
| ------ | -------------------------------- | ---------------------------- | -------------------- | --------------------------- |
| POST   | `/v1/admin/service/`             | Admin tạo dịch vụ            | `CreateServiceDto`   | `ClinicService`             |
| POST   | `/v1/admin/service/seed`         | Seed dữ liệu dịch vụ         | —                    | —                           |
| GET    | `/v1/admin/service/`             | Admin lấy danh sách dịch vụ  | Query lọc/phân trang | `ApiPaged<ClinicService>`   |
| GET    | `/v1/admin/service/count`        | Admin đếm dịch vụ            | Query lọc            | `Long`                      |
| GET    | `/v1/admin/service/{id}`         | Admin lấy chi tiết dịch vụ   | `id`                 | `ClinicService`             |
| PATCH  | `/v1/admin/service/{id}`         | Admin cập nhật dịch vụ       | `UpdateServiceDto`   | `ClinicService`             |
| PATCH  | `/v1/admin/service/restore/{id}` | Admin khôi phục dịch vụ      | `id`                 | `ClinicService`             |
| DELETE | `/v1/admin/service/{id}`         | Admin xóa dịch vụ            | `id`                 | `204 No Content`            |
| GET    | `/v1/public/service/`            | Public lấy danh sách dịch vụ | Query lọc/phân trang | `ApiPaged<ClinicService>`   |
| GET    | `/v1/public/service/count`       | Public đếm dịch vụ           | Query lọc            | `Long`                      |
| GET    | `/v1/public/service/{id}`        | Public lấy chi tiết dịch vụ  | `id`                 | `ClinicService`             |

---

## Review

| Method | Path                                             | Mô tả                              | Request DTO          | Response                |
| ------ | ------------------------------------------------ | ---------------------------------- | -------------------- | ----------------------- |
| POST   | `/v1/admin/review/`                              | Admin tạo đánh giá                 | `CreateReviewDto`    | `ReviewDto`             |
| GET    | `/v1/admin/review/`                              | Admin lấy danh sách đánh giá       | Query lọc/phân trang | `ApiPaged<ReviewDto>`   |
| GET    | `/v1/admin/review/count`                         | Admin đếm đánh giá                 | Query lọc            | `Long`                  |
| GET    | `/v1/admin/review/{id}`                          | Admin lấy chi tiết đánh giá        | `id`                 | `ReviewDto`             |
| PATCH  | `/v1/admin/review/{id}`                          | Admin cập nhật đánh giá            | `UpdateReviewDto`    | `ReviewDto`             |
| DELETE | `/v1/admin/review/{id}`                          | Admin xóa đánh giá                 | `id`                 | `204 No Content`        |
| GET    | `/v1/doctor/review/`                             | Doctor lấy danh sách đánh giá      | Query lọc/phân trang | `ApiPaged<ReviewDto>`   |
| GET    | `/v1/doctor/review/count`                        | Doctor đếm đánh giá                | Query lọc            | `Long`                  |
| GET    | `/v1/doctor/review/{id}`                         | Doctor lấy chi tiết đánh giá       | `id`                 | `ReviewDto`             |
| GET    | `/v1/staff/review/`                              | Staff lấy danh sách đánh giá       | Query lọc/phân trang | `ApiPaged<ReviewDto>`   |
| GET    | `/v1/staff/review/count`                         | Staff đếm đánh giá                 | Query lọc            | `Long`                  |
| GET    | `/v1/staff/review/{id}`                          | Staff lấy chi tiết đánh giá        | `id`                 | `ReviewDto`             |
| DELETE | `/v1/staff/review/{id}`                          | Staff xóa đánh giá                 | `id`                 | `204 No Content`        |
| POST   | `/v1/patient/review/`                            | Patient tạo đánh giá               | `CreateReviewDto`    | `ReviewDto`             |
| GET    | `/v1/patient/review/`                            | Patient lấy danh sách đánh giá     | Query lọc/phân trang | `ApiPaged<ReviewDto>`   |
| GET    | `/v1/patient/review/count`                       | Patient đếm đánh giá               | Query lọc            | `Long`                  |
| GET    | `/v1/patient/review/{id}`                        | Patient lấy chi tiết đánh giá      | `id`                 | `ReviewDto`             |
| GET    | `/v1/patient/review/appointment/{appointmentId}` | Patient lấy đánh giá theo lịch hẹn | `appointmentId`      | `ReviewDto`             |
| PATCH  | `/v1/patient/review/{id}`                        | Patient cập nhật đánh giá          | `UpdateReviewDto`    | `ReviewDto`             |
| DELETE | `/v1/patient/review/{id}`                        | Patient xóa đánh giá               | `id`                 | `204 No Content`        |
| GET    | `/v1/public/review/`                             | Public lấy danh sách đánh giá      | Query lọc/phân trang | `ApiPaged<ReviewDto>`   |
| GET    | `/v1/public/review/count`                        | Public đếm đánh giá                | Query lọc            | `Long`                  |
| GET    | `/v1/public/review/{id}`                         | Public lấy chi tiết đánh giá       | `id`                 | `ReviewDto`             |

---

## Promotion

| Method | Path                               | Mô tả                            | Request DTO            | Response                  |
| ------ | ---------------------------------- | -------------------------------- | ---------------------- | ------------------------- |
| POST   | `/v1/admin/promotion/`             | Admin tạo khuyến mãi             | `CreatePromotionDto`   | `Promotion`               |
| GET    | `/v1/admin/promotion/`             | Admin lấy danh sách khuyến mãi   | Query lọc/phân trang   | `ApiPaged<Promotion>`     |
| GET    | `/v1/admin/promotion/count`        | Admin đếm khuyến mãi             | Query lọc              | `Long`                    |
| GET    | `/v1/admin/promotion/{id}`         | Admin lấy chi tiết khuyến mãi    | `id`                   | `Promotion`               |
| PATCH  | `/v1/admin/promotion/{id}`         | Admin cập nhật khuyến mãi        | `UpdatePromotionDto`   | `Promotion`               |
| PATCH  | `/v1/admin/promotion/restore/{id}` | Admin khôi phục khuyến mãi       | `id`                   | `Promotion`               |
| DELETE | `/v1/admin/promotion/{id}`         | Admin xóa khuyến mãi             | `id`                   | `204 No Content`          |
| POST   | `/v1/staff/promotion/`             | Staff tạo khuyến mãi             | `CreatePromotionDto`   | `Promotion`               |
| GET    | `/v1/staff/promotion/`             | Staff lấy danh sách khuyến mãi   | Query lọc/phân trang   | `ApiPaged<Promotion>`     |
| GET    | `/v1/staff/promotion/count`        | Staff đếm khuyến mãi             | Query lọc              | `Long`                    |
| GET    | `/v1/staff/promotion/{id}`         | Staff lấy chi tiết khuyến mãi    | `id`                   | `Promotion`               |
| PATCH  | `/v1/staff/promotion/{id}`         | Staff cập nhật khuyến mãi        | `UpdatePromotionDto`   | `Promotion`               |
| PATCH  | `/v1/staff/promotion/restore/{id}` | Staff khôi phục khuyến mãi       | `id`                   | `Promotion`               |
| DELETE | `/v1/staff/promotion/{id}`         | Staff xóa khuyến mãi             | `id`                   | `204 No Content`          |
| GET    | `/v1/patient/promotion/`           | Patient lấy danh sách khuyến mãi | Query lọc/phân trang   | `ApiPaged<Promotion>`     |
| GET    | `/v1/patient/promotion/count`      | Patient đếm khuyến mãi           | Query lọc              | `Long`                    |
| GET    | `/v1/patient/promotion/{id}`       | Patient lấy chi tiết khuyến mãi  | `id`                   | `Promotion`               |

---

## Message

### Patient Message

| Method | Path                               | Mô tả                          | Request DTO           | Response                     |
| ------ | ---------------------------------- | ------------------------------ | --------------------- | ---------------------------- |
| POST   | `/v1/patient/message/`             | Patient tạo tin nhắn           | `CreateMessageDto`    | `ResponseMessageDto`         |
| GET    | `/v1/patient/message/`             | Patient lấy danh sách tin nhắn | Query lọc/phân trang  | `ApiPaged<ResponseMessageDto>` |
| GET    | `/v1/patient/message/count`        | Patient đếm tin nhắn           | Query lọc             | `Long`                       |
| GET    | `/v1/patient/message/{id}`         | Patient lấy chi tiết tin nhắn  | `id`                  | `ResponseMessageDto`         |
| PATCH  | `/v1/patient/message/{id}`         | Patient cập nhật tin nhắn      | `UpdateMessageDto`    | `ResponseMessageDto`         |
| PATCH  | `/v1/patient/message/restore/{id}` | Patient khôi phục tin nhắn     | `id`                  | `ResponseMessageDto`         |
| DELETE | `/v1/patient/message/{id}`         | Patient xóa tin nhắn           | `id`                  | `204 No Content`             |

### Doctor Message

| Method | Path                              | Mô tả                         | Request DTO           | Response                       |
| ------ | --------------------------------- | ----------------------------- | --------------------- | ------------------------------ |
| POST   | `/v1/doctor/message/`             | Doctor tạo tin nhắn           | `CreateMessageDto`    | `ResponseMessageDto`           |
| GET    | `/v1/doctor/message/`             | Doctor lấy danh sách tin nhắn | Query lọc/phân trang  | `ApiPaged<ResponseMessageDto>` |
| GET    | `/v1/doctor/message/count`        | Doctor đếm tin nhắn           | Query lọc             | `Long`                         |
| GET    | `/v1/doctor/message/{id}`         | Doctor lấy chi tiết tin nhắn  | `id`                  | `ResponseMessageDto`           |
| PATCH  | `/v1/doctor/message/{id}`         | Doctor cập nhật tin nhắn      | `UpdateMessageDto`    | `ResponseMessageDto`           |
| PATCH  | `/v1/doctor/message/restore/{id}` | Doctor khôi phục tin nhắn     | `id`                  | `ResponseMessageDto`           |
| DELETE | `/v1/doctor/message/{id}`         | Doctor xóa tin nhắn           | `id`                  | `204 No Content`               |

---

## Medical Record

| Method | Path                                     | Mô tả                               | Request DTO                | Response                    |
| ------ | ---------------------------------------- | ----------------------------------- | -------------------------- | --------------------------- |
| POST   | `/v1/doctor/medical-record/`             | Doctor tạo hồ sơ bệnh án            | `CreateMedicalRecordDto`   | `MedicalRecord`             |
| GET    | `/v1/doctor/medical-record/`             | Doctor lấy danh sách hồ sơ bệnh án  | Query lọc/phân trang       | `ApiPaged<MedicalRecord>`   |
| GET    | `/v1/doctor/medical-record/count`        | Doctor đếm hồ sơ bệnh án            | Query lọc                  | `Long`                      |
| GET    | `/v1/doctor/medical-record/{id}`         | Doctor lấy chi tiết hồ sơ bệnh án   | `id`                       | `MedicalRecord`             |
| PATCH  | `/v1/doctor/medical-record/{id}`         | Doctor cập nhật hồ sơ bệnh án       | `UpdateMedicalRecordDto`   | `MedicalRecord`             |
| PATCH  | `/v1/doctor/medical-record/restore/{id}` | Doctor khôi phục hồ sơ bệnh án      | `id`                       | `MedicalRecord`             |
| DELETE | `/v1/doctor/medical-record/{id}`         | Doctor xóa hồ sơ bệnh án            | `id`                       | `204 No Content`            |
| GET    | `/v1/patient/medical-record/`            | Patient lấy danh sách hồ sơ bệnh án | Query lọc/phân trang       | `ApiPaged<MedicalRecord>`   |
| GET    | `/v1/patient/medical-record/count`       | Patient đếm hồ sơ bệnh án           | Query lọc                  | `Long`                      |
| GET    | `/v1/patient/medical-record/{id}`        | Patient lấy chi tiết hồ sơ bệnh án  | `id`                       | `MedicalRecord`             |

---

## Loyalty Transaction

| Method | Path                                         | Mô tả                                       | Request DTO                    | Response                            |
| ------ | -------------------------------------------- | ------------------------------------------- | ------------------------------ | ----------------------------------- |
| POST   | `/v1/staff/loyalty-transaction/`             | Staff tạo giao dịch điểm thưởng             | `CreateLoyaltyTransactionDto`  | `LoyaltyTransaction`                |
| GET    | `/v1/staff/loyalty-transaction/`             | Staff lấy danh sách giao dịch điểm thưởng   | Query lọc/phân trang           | `ApiPaged<LoyaltyTransaction>`      |
| GET    | `/v1/staff/loyalty-transaction/count`        | Staff đếm giao dịch điểm thưởng             | Query lọc                      | `Long`                              |
| GET    | `/v1/staff/loyalty-transaction/{id}`         | Staff lấy chi tiết giao dịch điểm thưởng    | `id`                           | `LoyaltyTransaction`                |
| PATCH  | `/v1/staff/loyalty-transaction/{id}`         | Staff cập nhật giao dịch điểm thưởng        | `UpdateLoyaltyTransactionDto`  | `LoyaltyTransaction`                |
| PATCH  | `/v1/staff/loyalty-transaction/restore/{id}` | Staff khôi phục giao dịch điểm thưởng       | `id`                           | `LoyaltyTransaction`                |
| DELETE | `/v1/staff/loyalty-transaction/{id}`         | Staff xóa giao dịch điểm thưởng             | `id`                           | `204 No Content`                    |
| GET    | `/v1/patient/loyalty-transaction/`           | Patient lấy danh sách giao dịch điểm thưởng | Query lọc/phân trang           | `ApiPaged<LoyaltyTransaction>`      |
| GET    | `/v1/patient/loyalty-transaction/count`      | Patient đếm giao dịch điểm thưởng           | Query lọc                      | `Long`                              |
| GET    | `/v1/patient/loyalty-transaction/{id}`       | Patient lấy chi tiết giao dịch điểm thưởng  | `id`                           | `LoyaltyTransaction`                |
| GET    | `/v1/patient/loyalty-transaction/statistics` | Patient lấy thống kê điểm thưởng            | Query thời gian/lọc            | `LoyaltyTransactionStatisticsDto`   |

---

## Invoice

| Method | Path                                            | Mô tả                           | Request DTO          | Response                |
| ------ | ----------------------------------------------- | ------------------------------- | -------------------- | ----------------------- |
| POST   | `/v1/staff/invoice/`                            | Staff tạo hóa đơn               | `CreateInvoiceDto`   | `Invoice`               |
| GET    | `/v1/staff/invoice/`                            | Staff lấy danh sách hóa đơn     | Query lọc/phân trang | `ApiPaged<Invoice>`     |
| GET    | `/v1/staff/invoice/count`                       | Staff đếm hóa đơn               | Query lọc            | `Long`                  |
| GET    | `/v1/staff/invoice/{id}`                        | Staff lấy chi tiết hóa đơn      | `id`                 | `Invoice`               |
| GET    | `/v1/staff/invoice/appointment/{appointmentId}` | Staff lấy hóa đơn theo lịch hẹn | `appointmentId`      | `Invoice`               |
| PATCH  | `/v1/staff/invoice/{id}`                        | Staff cập nhật hóa đơn          | `UpdateInvoiceDto`   | `Invoice`               |
| PATCH  | `/v1/staff/invoice/restore/{id}`                | Staff khôi phục hóa đơn         | `id`                 | `Invoice`               |
| DELETE | `/v1/staff/invoice/{id}`                        | Staff xóa hóa đơn               | `id`                 | `204 No Content`        |
| POST   | `/v1/patient/invoice/`                          | Patient tạo hóa đơn             | `CreateInvoiceDto`   | `Invoice`               |
| GET    | `/v1/patient/invoice/`                          | Patient lấy danh sách hóa đơn   | Query lọc/phân trang | `ApiPaged<Invoice>`     |
| GET    | `/v1/patient/invoice/count`                     | Patient đếm hóa đơn             | Query lọc            | `Long`                  |
| GET    | `/v1/patient/invoice/{id}`                      | Patient lấy chi tiết hóa đơn    | `id`                 | `Invoice`               |
| PATCH  | `/v1/patient/invoice/{id}`                      | Patient cập nhật hóa đơn        | `UpdateInvoiceDto`   | `Invoice`               |

---

## FAQ

| Method | Path                         | Mô tả                    | Request DTO      | Response           |
| ------ | ---------------------------- | ------------------------ | ---------------- | ------------------ |
| POST   | `/v1/admin/faq/`             | Admin tạo FAQ            | `CreateFaqDto`   | `Faq`              |
| GET    | `/v1/admin/faq/`             | Admin lấy danh sách FAQ  | Query lọc/phân trang | `ApiPaged<Faq>` |
| GET    | `/v1/admin/faq/count`        | Admin đếm FAQ            | Query lọc        | `Long`             |
| GET    | `/v1/admin/faq/{id}`         | Admin lấy chi tiết FAQ   | `id`             | `Faq`              |
| PATCH  | `/v1/admin/faq/{id}`         | Admin cập nhật FAQ       | `UpdateFaqDto`   | `Faq`              |
| PATCH  | `/v1/admin/faq/restore/{id}` | Admin khôi phục FAQ      | `id`             | `Faq`              |
| DELETE | `/v1/admin/faq/{id}`         | Admin xóa FAQ            | `id`             | `204 No Content`   |
| GET    | `/v1/public/faq/`            | Public lấy danh sách FAQ | Query lọc/phân trang | `ApiPaged<Faq>` |
| GET    | `/v1/public/faq/count`       | Public đếm FAQ           | Query lọc        | `Long`             |
| GET    | `/v1/public/faq/{id}`        | Public lấy chi tiết FAQ  | `id`             | `Faq`              |

---

## User

| Method | Path                          | Mô tả                       | Request DTO          | Response             |
| ------ | ----------------------------- | --------------------------- | -------------------- | -------------------- |
| POST   | `/v1/admin/user/`             | Admin tạo user              | `CreateUserDto`      | `User`               |
| GET    | `/v1/admin/user/`             | Admin lấy danh sách user    | Query lọc/phân trang | `ApiPaged<User>`     |
| GET    | `/v1/admin/user/count`        | Admin đếm user              | Query lọc            | `Long`               |
| GET    | `/v1/admin/user/statistics`   | Admin lấy thống kê user     | Query thời gian/lọc  | `UserStatisticsDto`  |
| GET    | `/v1/admin/user/{id}`         | Admin lấy chi tiết user     | `id`                 | `User`               |
| PATCH  | `/v1/admin/user/{id}`         | Admin cập nhật user         | `UpdateUserDto`      | `User`               |
| PATCH  | `/v1/admin/user/restore/{id}` | Admin khôi phục user đã xóa | `id`                 | `User`               |
| DELETE | `/v1/admin/user/{id}`         | Admin xóa user              | `id`                 | `204 No Content`     |
| GET    | `/v1/public/user/statistics`  | Public lấy thống kê user    | Query thời gian/lọc  | `UserStatisticsDto`  |
| GET    | `/v1/public/user/`            | Public lấy danh sách user   | Query lọc/phân trang | `ApiPaged<User>`     |

---

## Profile

| Method | Path                  | Mô tả                        | Request DTO          | Response            |
| ------ | --------------------- | ---------------------------- | -------------------- | ------------------- |
| GET    | `/v1/public/profile/` | Public lấy danh sách profile | Query lọc/phân trang | `List<User/Profile data>` |

---

## Presence

| Method | Path                   | Mô tả                                     | Request DTO          | Response                   |
| ------ | ---------------------- | ----------------------------------------- | -------------------- | -------------------------- |
| GET    | `/v1/public/presence/` | Public lấy danh sách trạng thái hiện diện | Query lọc/phân trang | `List<PresenceDto>`        |

---

## Landing

| Method | Path                  | Mô tả                     | Request DTO             | Response                 |
| ------ | --------------------- | ------------------------- | ----------------------- | ------------------------ |
| GET    | `/v1/public/landing/` | Lấy dữ liệu trang landing | Không có hoặc query lọc | `ResponseLandingDto`     |

---

## Clinic Information

| Method | Path                             | Mô tả                    | Request DTO | Response                    |
| ------ | -------------------------------- | ------------------------ | ----------- | --------------------------- |
| GET    | `/v1/public/clinic-information/` | Lấy thông tin phòng khám | Không có    | `ResponseClinicInformation` |
