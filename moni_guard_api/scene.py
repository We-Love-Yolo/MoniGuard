

class Scene:

    def __init__(self, scene_id: int, name: str, resident_id: int):
        """
        :param scene_id: 场景 ID。
        :param name: 场景名称。
        :param resident_id: 归属用户 ID。
        """
        self.scene_id = scene_id
        self.name = name
        self.resident_id = resident_id

    def __str__(self):
        return f"Scene(scene_id={self.scene_id}, name={self.name}, resident_id={self.resident_id})"

    def __repr__(self):
        return str(self)

