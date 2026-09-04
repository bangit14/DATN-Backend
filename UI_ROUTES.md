# 🧭 Danh Sách Đường Dẫn Các Màn Hình Giao Diện (Frontend Routes)

Hệ thống Frontend (Job Portal FE) được xây dựng trên nền tảng **Vite + React Router v7** theo phong cách thiết kế **Stitch IT-Job Portal (ITviec / Tech-Trust)** với các Layout và phân quyền theo từng vai trò (Role).

---

## 🌐 1. Màn Hình Công Khai / Khách (Public & Common)
*Layout: `MainLayout`*

| Đường dẫn (Route URL) | Tên màn hình | File Component | Mô tả chức năng & Thiết kế |
| :--- | :--- | :--- | :--- |
| `/` | **Trang chủ** | `views/common/HomePage.jsx` | Hero tìm kiếm việc làm IT, từ khóa thịnh hành, Top doanh nghiệp IT, danh sách việc làm mới nhất. |
| `/jobs` hoặc `/student/jobs` | **Khám phá Việc làm IT** | `views/student/JobList.jsx` | Bộ lọc đa tiêu chí (Địa điểm, Cấp bậc, Hình thức, Kỹ năng), thẻ Job Card có tags, AI Matching. |
| `/jobs/:id` | **Chi tiết Việc làm** | `views/student/JobDetail.jsx` | Xem JD, quyền lợi, yêu cầu kỹ năng, sidebar công ty và modal nộp CV trực tiếp. |
| `/companies` | **Danh sách Công ty IT** | `views/common/CompanyList.jsx` | Thẻ công ty công nghệ, quy mô, địa điểm, số lượng việc làm đang mở. |
| `/interview-practice` | **Luyện tập Phỏng vấn IT** | `views/common/InterviewPractice.jsx` | Ngân hàng câu hỏi phỏng vấn kỹ thuật theo chuyên ngành (Java, React, SQL, Microservices) và cấp độ. |
| `/p/:id` | **Hồ sơ Sinh viên Công khai** | `views/student/PublicStudentProfile.jsx` | Xem profile công khai của sinh viên qua ID. |

---

## 🔐 2. Màn Hình Xác Thực & Tài Khoản (Authentication)
*Layout: `AuthLayout`*

| Đường dẫn (Route URL) | Tên màn hình | File Component | Mô tả chức năng & Thiết kế |
| :--- | :--- | :--- | :--- |
| `/auth/login` | **Đăng nhập** | `views/auth/Login.jsx` | Đăng nhập tài khoản bằng Email/Mật khẩu hoặc Google OAuth. |
| `/auth/register` | **Đăng ký** | `views/auth/Register.jsx` | Đăng ký tài khoản mới với Tab chuyển đổi Ứng viên / Nhà tuyển dụng. |
| `/auth/forgotpassword` | **Quên mật khẩu** | `views/auth/ForgotPassword.jsx` | Gửi yêu cầu đặt lại mật khẩu qua email. |

---

## 🎓 3. Màn Hình Dành Cho Ứng Viên / Sinh Viên (Candidate Portal)
*Layout: `StudentLayout` (Yêu cầu đăng nhập, Role: `STUDENT`)*

| Đường dẫn (Route URL) | Tên màn hình | File Component | Mô tả chức năng & Thiết kế |
| :--- | :--- | :--- | :--- |
| `/student/profile` | **Tổng quan Hồ sơ** | `views/student/StudentProfile.jsx` | Dashboard ứng viên, % hoàn thiện hồ sơ, KPI hoạt động, kỹ năng, học vấn, kinh nghiệm. |
| `/student/cv-management` | **Hồ sơ đính kèm (CV)** | `views/student/CVManagement.jsx` | Quản lý file CV tải lên (PDF), đổi tên, đặt CV mặc định, thư xin việc (cover letter). |
| `/student/cv-templates` | **Thư viện Mẫu CV IT** | `views/student/CVTemplates.jsx` | Bộ sưu tập mẫu CV chuẩn IT (Modern Tech, Minimal ATS, Fresher PTIT, Senior Architect). |
| `/student/cv-builder` | **Tạo CV Trực tuyến** | `views/student/CVBuilder.jsx` | Trình soạn thảo CV online, Live Preview A4 thời gian thực và xuất file PDF. |
| `/student/my-cvs` | **Quản lý CV Trực tuyến** | `views/student/MyOnlineCVs.jsx` | Danh sách các bản CV online đã tạo, sửa đổi nội dung và tải về. |
| `/student/my-applications` | **Việc làm của tôi** | `views/student/AppliedJobs.jsx` | Quản lý theo 3 tab: Đã ứng tuyển, Đã lưu, Đã xem gần đây kèm trạng thái xét duyệt. |
| `/student/job-invitations` | **Lời mời công việc** | `views/student/JobInvitations.jsx` | Quản lý lời mời phỏng vấn & kết nối ẩn danh từ Nhà tuyển dụng (Chấp nhận / Từ chối). |
| `/student/settings` | **Cài đặt tài khoản** | `views/student/AccountSettings.jsx` | Bật/tắt cho phép NTD tìm kiếm CV, danh sách công ty chặn, đổi mật khẩu. |

---

## 🏢 4. Màn Hình Dành Cho Nhà Tuyển Dụng (Employer Portal)
*Layout: `EmployerLayout` (Yêu cầu đăng nhập, Role: `EMPLOYER`)*

| Đường dẫn (Route URL) | Tên màn hình | File Component | Mô tả chức năng & Thiết kế |
| :--- | :--- | :--- | :--- |
| `/employer/dashboard` | **Bảng điều khiển** | `views/employer/Dashboard.jsx` | Thống kê số lượng tin đăng, số lượt nộp hồ sơ, CV chờ duyệt và mẹo tuyển dụng. |
| `/employer/onboarding` | **Thiết lập Công ty** | `views/employer/EmployerOnboarding.jsx` | Cập nhật thông tin công ty lần đầu sau khi đăng ký tài khoản. |
| `/employer/profile` | **Hồ sơ Doanh nghiệp** | `views/employer/EmployerProfile.jsx` | Quản lý thương hiệu, logo, quy mô, địa chỉ, mô tả, website. |
| `/employer/create-job` | **Đăng tin Tuyển dụng** | `views/employer/CreateJob.jsx` | Tạo bài đăng tuyển dụng mới (tiêu đề, mô tả, yêu cầu kỹ năng, mức lương, hạn nộp). |
| `/employer/my-jobs` | **Quản lý Tin tuyển dụng** | `views/employer/MyJobs.jsx` | Danh sách bài đăng của công ty, trạng thái duyệt (Pending, Approved, Rejected). |
| `/employer/jobs/:id` | **Chi tiết Tin đã đăng** | `views/employer/JobDetail.jsx` | Xem chi tiết bài tuyển dụng do chính công ty đăng tải. |
| `/employer/jobs/:id/edit` | **Chỉnh sửa Tin đăng** | `views/employer/JobEdit.jsx` | Cập nhật nội dung bài đăng tuyển dụng. |
| `/employer/posts/:postId/applications` | **Danh sách Ứng viên** | `views/employer/PostApplications.jsx` | Danh sách ứng viên ứng tuyển vào bài đăng, xem CV, cập nhật trạng thái duyệt. |
| `/employer/candidates/:id` | **Chi tiết Ứng viên** | `views/employer/CandidateDetail.jsx` | Xem chi tiết hồ sơ năng lực và CV của ứng viên. |

---

## 🛡️ 5. Màn Hình Dành Cho Quản Trị Viên (Admin Portal)
*Layout: `AdminLayout` (Yêu cầu đăng nhập, Role: `ADMIN`, `SYSTEM_ADMIN`)*

| Đường dẫn (Route URL) | Tên màn hình | File Component | Mô tả chức năng & Thiết kế |
| :--- | :--- | :--- | :--- |
| `/admin/dashboard` | **Tổng quan Quản trị** | `views/admin/AdminDashboard.jsx` | Báo cáo số liệu toàn hệ thống (User, Công ty, Tin tuyển dụng, CV, Tin chờ duyệt). |
| `/admin/users` | **Quản lý Người dùng** | `views/admin/UserTables.jsx` | Quản lý Ứng viên và Doanh nghiệp, phân quyền, kích hoạt / khóa tài khoản. |
| `/admin/job-posts` | **Quản lý Bài đăng** | `views/admin/JobPostManagement.jsx` | Xem và quản lý tất cả các bài tuyển dụng trên toàn hệ thống. |
| `/admin/job-approval` | **Phê duyệt Bài đăng** | `views/admin/AdminJobApproval.jsx` | Duyệt hoặc từ chối các tin tuyển dụng mới do Doanh nghiệp gửi lên. |
| `/admin/job-hidden` | **Bài đăng Bị ẩn / Từ chối** | `views/admin/AdminRejectedHiddenJobsTable.jsx` | Danh sách các bài đăng bị từ chối hoặc ẩn khỏi trang chủ. |
| `/admin/skills` | **Quản lý Kỹ năng** | `views/admin/SkillManagement.jsx` | Thêm, sửa, xóa danh mục kỹ năng chuyên môn trong hệ thống. |
| `/admin/settings` | **Cài đặt Quản trị** | `views/admin/Settings.jsx` | Cấu hình cài đặt hệ thống. |

---

## 💬 6. Thành Phần Nổi & Hệ Thống Thiết Kế (Design System)
- **Bảng màu chủ đạo**: Primary `#00B14F` (Xanh lá ITviec), Secondary `#2E6BE6` (Xanh dương Tech), Accent `#F5A623` (Vàng gold).
- **Phông chữ**: `Be Vietnam Pro` (Google Fonts), tối ưu hiển thị tiếng Việt.
- **Chat Widget (`components/Chat/ChatWidget.jsx`)**: Hộp thoại nhắn tin trực tiếp theo thời gian thực (WebSocket) giữa Sinh viên và Nhà tuyển dụng.
- **Sidebar Tài khoản Ứng viên (`components/Sidebar/CandidateAccountSidebar.jsx`)**: Menu dọc đồng bộ xuyên suốt các trang trong phân hệ Ứng viên.
