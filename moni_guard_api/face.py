import datetime


class Face:
    def __init__(self, face_id: int, guest_id: int, name: str, captured_at: datetime, hash: bytes):
        """
        :param face_id: 人脸 ID。
        :param guest_id: 访客 ID。
        :param name: 人脸名称。
        :param captured_at: 人脸捕获时间。
        :param hash: 人脸哈希值。
        """
        self.face_id = face_id
        self.guest_id = guest_id
        self.name = name
        self.captured_at = captured_at
        self.hash = hash

    def __str__(self):
        return f"Face(face_id={self.face_id}, face_encoding={self.face_encoding}, face_image={self.face_image})"

    def __repr__(self):
        return str(self)