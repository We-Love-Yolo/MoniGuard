class Photo:
    def __init__(self, photo_id: int, camera_id: int, created_at: str, name: str):
        """
        :param photo_id: 照片 ID。
        :param camera_id: 拍摄摄像机 ID。
        :param created_at: 创建时间。
        :param name: 照片名字。
        """
        self.photo_id = photo_id
        self.camera_id = camera_id
        self.created_at = created_at
        self.name = name


    def __str__(self):
        return f"Photo(photo_id={self.photo_id}, camera_id={self.camera_id}, created_at={self.created_at}, name={self.name})"

    def __repr__(self):
        return str(self)
