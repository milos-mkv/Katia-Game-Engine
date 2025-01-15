local Behaviour = classes.class()
require("Constants")
function Behaviour:init(params)
    print("Behaviour:init")
    self.gameObject = params.gameObject
    self.scene = params.scene
    for k, v in pairs(params) do
        print(k, v)
    end
    self.camera = self.scene:find("Main Camera")
    self.pos = self.camera:getComponent("Transform"):getPosition()
    self.rot = self.camera:getComponent("Transform"):getRotation()
end


function Behaviour:update(dt)
    -- Update
  --  self.rot = self.rot +0.01
    self.gameObject:getComponent("Transform"):setRotation(self.rot)
    if InputManager:isKeyPressed(KEY_A) then
        self.pos.x = self.pos.x - 1
    end
    if InputManager:isKeyPressed(KEY_D) then
        self.pos.x = self.pos.x + 1
    end
    if InputManager:isKeyPressed(KEY_W) then
        self.pos.y = self.pos.y + 1
    end
    if InputManager:isKeyPressed(KEY_S) then
        self.pos.y = self.pos.y - 1
    end

    if InputManager:isKeyJustPressed(KEY_F) then
        print(SceneManager:getActiveScene(), "Behaviour:init", 'SAD',"Behaviour:init", 1,"Behaviour:init", 'SAD')
    end
end

return Behaviour