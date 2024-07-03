from datetime import datetime


class Guest:
    def __init__(self, guest_id: int, scene_id: int, name: str, created_at: datetime, is_whitelisted: bool):
        """
        :param guest_id: 访客 ID。
        :param scene_id: 场景 ID。
        :param name: 访客名称。
        :param created_at: 访客创建时间。
        :param is_whitelisted: 访客是否允许。
        """
        self.guest_id = guest_id
        self.scene_id = scene_id
        self.name = name
        self.created_at = created_at
        self.is_whitelisted = is_whitelisted

    def __str__(self):
        return f"Guest(guest_id={self.guest_id}, scene_id={self.scene_id}, name={self.name}, created_at={self.created_at}, is_whitelisted={self.is_whitelisted})"

    def __repr__(self):
        return str(self)

    @staticmethod
    def from_json(json):
        return [Guest(guest_id=guest_object['guestId'], scene_id=guest_object['sceneId'], name=guest_object['name'],
                      created_at=datetime.strptime(guest_object['createdAt'][:-1], "%Y-%m-%dT%H:%M:%S.%f"),
                      is_whitelisted=guest_object['isWhitelisted']) for guest_object in json]
