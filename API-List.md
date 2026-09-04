# API List

## Auth Service
| Method | Endpoint | Mô tả | Role |
| ------ | -------- | ----- | ---- |
| POST | `/api/auth/register` | Đăng ký tài khoản mới (candidate / HR) | Public |
| POST | `/api/auth/login` | Đăng nhập, trả về access token + refresh token | Public |
| POST | `/api/auth/refresh` | Làm mới access token bằng refresh token | Public |
| POST | `/api/auth/logout` | Huỷ session, revoke refresh token | Auth |
| POST | `/api/auth/forgot-password` | Gửi email reset mật khẩu | Public |
| POST | `/api/auth/reset-password` | Đặt lại mật khẩu bằng token email | Public |
| POST | `/api/auth/verify-email` | Xác thực email sau đăng ký | Public |
| POST | `/api/auth/oauth2/google` | Đăng nhập bằng Google OAuth2 | Public |

## User Service
| GET | `/api/users/me` | Lấy thông tin profile của chính mình | Auth |
| PUT | `/api/users/me` | Cập nhật thông tin profile | Auth |
| PUT | `/api/users/me/password` | Đổi mật khẩu | Auth |
| PUT | `/api/users/me/avatar` | Upload / thay đổi ảnh đại diện | Auth |
| GET | `/api/users/{id}` | Lấy public profile của user theo ID | Auth |
| GET | `/api/admin/users` | Lấy danh sách tất cả user (có filter, pagination) | Admin |
| GET | `/api/admin/users/{id}` | Xem chi tiết user bất kỳ | Admin |
| PUT | `/api/admin/users/{id}/status` | Kích hoạt / khoá tài khoản user | Admin |
| PUT | `/api/admin/users/{id}/role` | Thay đổi role của user | Admin |
| DELETE | `/api/admin/users/{id}` | Xoá tài khoản user | Admin |

## Job Service
| Method | Endpoint | Mô tả | Role |
| ------ | -------- | ----- | ---- |
| GET | `/api/jobs` | Danh sách job (filter: location, skill, salary, type) | Public |
| GET | `/api/jobs/{id}` | Chi tiết một job description | Public |
| GET | `/api/jobs/search` | Tìm kiếm full-text job (Elasticsearch) | Public |
| GET | `/api/jobs/recommendations` | Gợi ý job phù hợp với profile candidate | Candidate |
| POST | `/api/jobs` | Tạo job description mới | HR |
| PUT | `/api/jobs/{id}` | Cập nhật nội dung job description | HR |
| PATCH | `/api/jobs/{id}/status` | Mở / đóng / hết hạn một job | HR |
| DELETE | `/api/jobs/{id}` | Xoá job (soft delete) | HR |
| GET | `/api/hr/jobs` | Danh sách job do HR này tạo | HR |
| GET | `/api/hr/jobs/{id}/stats` | Thống kê: lượt xem, số ứng tuyển | HR |
| GET | `/api/admin/jobs` | Quản lý tất cả job trong hệ thống | Admin |
| PATCH | `/api/admin/jobs/{id}/status` | Duyệt / từ chối / ẩn job | Admin |
| GET | `/api/categories` | Lấy danh sách ngành nghề / categories | Public |
| GET | `/api/skills` | Lấy danh sách skill gợi ý | Public |

## Application Service
| Method | Endpoint | Mô tả | Role |
| ------ | -------- | ----- | ---- |
| POST | `/api/applications` | Candidate nộp đơn ứng tuyển một job | Candidate |
| GET | `/api/applications/me` | Danh sách đơn ứng tuyển của candidate đang đăng nhập | Candidate |
| GET | `/api/applications/{id}` | Chi tiết một đơn ứng tuyển | Candidate, HR |
| DELETE | `/api/applications/{id}` | Rút đơn ứng tuyển (nếu chưa được xử lý) | Candidate |
| GET | `/api/hr/applications` | Danh sách tất cả đơn của các job HR quản lý | HR |
| GET | `/api/hr/jobs/{jobId}/applications` | Danh sách đơn theo một job cụ thể | HR |
| PATCH | `/api/hr/applications/{id}/status` | Cập nhật trạng thái đơn (screening, interview, offer...) | HR |
| POST | `/api/hr/applications/{id}/notes` | Thêm ghi chú internal về candidate | HR |
| GET | `/api/hr/applications/{id}/notes` | Lấy danh sách ghi chú về candidate | HR |
| PUT | `/api/hr/applications/{id}/score` | Đánh giá điểm thủ công cho candidate | HR |
| GET | `/api/admin/applications` | Xem tất cả đơn trong hệ thống | Admin |

## Matching Service
| Method | Endpoint | Mô tả | Role |
| ------ | -------- | ----- | ---- |
| GET | `/api/matching/job/{jobId}/candidates` | Gợi ý candidate phù hợp nhất cho một job | HR |
| GET | `/api/matching/candidate/{candidateId}/jobs` | Gợi ý job phù hợp nhất cho một candidate | HR, Candidate |
| GET | `/api/matching/applications/{appId}/score` | Lấy điểm matching AI cho đơn ứng tuyển | HR |
| POST | `/api/matching/batch` | Tính matching score hàng loạt (batch job) | HR, Admin |

## CV Service
| Method | Endpoint | Mô tả | Role |
| ------ | -------- | ----- | ---- |
| POST | `/api/files/upload/cv` | Upload file CV (PDF, DOC, DOCX) | Candidate |
| POST | `/api/files/upload/avatar` | Upload ảnh đại diện | Auth |
| POST | `/api/files/upload/attachment` | Upload tài liệu đính kèm (portfolio, certificate) | Candidate |
| GET | `/api/files/{fileId}` | Tải xuống / xem file | Auth |
| DELETE | `/api/files/{fileId}` | Xoá file đã upload | Auth |

## Notification Service
| Method | Endpoint | Mô tả | Role |
| ------ | -------- | ----- | ---- |
| GET | `/api/notifications` | Danh sách thông báo của user hiện tại | Auth |
| PATCH | `/api/notifications/{id}/read` | Đánh dấu một thông báo đã đọc | Auth |
| PATCH | `/api/notifications/read-all` | Đánh dấu tất cả thông báo đã đọc | Auth |
| DELETE | `/api/notifications/{id}` | Xoá một thông báo | Auth |
| GET | `/api/notifications/settings` | Lấy cài đặt thông báo (email, push) | Auth |
| PUT | `/api/notifications/settings` | Cập nhật cài đặt thông báo | Auth |

## Admin Stats Service
| Method | Endpoint | Mô tả | Role |
| ------ | -------- | ----- | ---- |
| GET | `/api/admin/stats/overview` | Tổng quan: số user, job, application hôm nay | Admin |
| GET | `/api/admin/stats/applications` | Biểu đồ đơn ứng tuyển theo thời gian | Admin |
| GET | `/api/admin/stats/jobs` | Thống kê job theo ngành, khu vực | Admin |
| GET | `/api/admin/stats/matching` | Hiệu suất AI matching theo tháng | Admin |
