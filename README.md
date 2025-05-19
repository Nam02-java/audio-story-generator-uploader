🇻🇳 Mô tả dự án (Tiếng Việt)
Hệ thống Tự Động Chuyển Truyện Thành Video/Audio và Đăng Lên YouTube

Dự án này là một hệ thống tự động xử lý truyện từ website, chuyển thành video audio chất lượng cao, và đăng lên YouTube theo từng cụm chương. Quy trình hoạt động như sau:

Nhận link truyện đầu vào từ một giao diện dòng lệnh (console).

Scan và tải ảnh nền từ nội dung truyện, sau đó làm nét ảnh (1920x1080) bằng mô hình Real-ESRGAN.

Trích xuất thông tin truyện: tiêu đề, tổng số chương, lưu vào cơ sở dữ liệu MySQL.

Phân chia chương theo cụm (ví dụ: mỗi video gồm 10 chương).

Chạy đa luồng (3 thread), mỗi luồng tương ứng 1 trình duyệt Google Chrome độc lập (port riêng qua Selenium).

Crawl nội dung chương → gửi đến website speech.aiservice.vn để chuyển văn bản thành giọng đọc → tải audio.

Nếu chương dài quá 8000 ký tự → tự động chia nhỏ chương và ghép audio lại sau khi tải về.

Ghép audio với ảnh nền tương ứng, xuất ra video MP4.

Sau khi có 10 video con, hệ thống gộp thành 1 video lớn (tổng hợp từ chương 1–10).

Tự động upload video lớn lên YouTube qua YouTube Data API v3.

Tiếp tục quy trình với các chương tiếp theo.

Với ảnh thumbnail trên Canva, nếu chưa sẵn có → hệ thống chờ người dùng bổ sung và xác nhận mới tiếp tục.

Youtube Channel link : https://www.youtube.com/@truongthanhnam2210

🇺🇸 Project Description (English)
Automated Audio Story-to-Video System with YouTube Uploader

This project is an automated pipeline that crawls stories from a website, converts them into high-quality audio-video files, and uploads the final compilation to YouTube. Here's the process:

Accept story links via a command-line interface.

Extract and enhance background images (1920x1080) using Real-ESRGAN.

Fetch story metadata (title, chapter count) and store in a MySQL database.

Split the story into chapter groups (e.g., 10 chapters per video).

Use multithreading (3 threads) where each thread controls a separate Chrome instance (Selenium port-specific).

Crawl story text and send to speech.aiservice.vn to generate voice audio → download audio file.

If a chapter is longer than 8000 characters → automatically split into chunks and merge after.

Combine audio with corresponding enhanced image to produce a short video.

After generating 10 mini videos, merge them into a single full-length video (e.g., Chapter 1–10).

Upload the final video to YouTube via YouTube Data API v3.

Continue this loop with remaining chapters.

For thumbnails from Canva: if not yet available, the system waits for user confirmation before proceeding.

Youtube Channel link : https://www.youtube.com/@truongthanhnam2210

