---------------------------------------------------------------------------------------------------------------------------------------
-- @file       GameLogic.lua
-- @author     Milos Milicevic (milosh.mkv@gmail.com)
-- 
-- @version     0.1
-- @date        2025-03-21
-- @copyright 	Copyright (c) 2025
--    
-- Distributed under the MIT software license, see the accompanying file LICENCE or http://www.opensource.org/licenses/mit-license.php.
---------------------------------------------------------------------------------------------------------------------------------------

local Behaviour = require("Behaviour")
local GameLogic = classes.class(Behaviour)
require("Constants")

TETRIMINOS = {
    T = {
        {
            { 0, 1, 0 },
            { 1, 1, 1 }
        },
        {
            { 0, 1, 0 },
            { 0, 1, 1 },
            { 0, 1, 0 }
        },
        {
            { 0, 0, 0 },
            { 1, 1, 1 },
            { 0, 1, 0 }
        },
        {
            { 0, 1 },
            { 1, 1 },
            { 0, 1 }
        }
    },

    L = {
        {
            { 0, 0, 0 },
            { 1, 1, 1 },
            { 1, 0, 0 }
        },
        {
            { 1, 1 },
            { 0, 1 },
            { 0, 1 }
        },
        {
            { 0, 0, 1 },
            { 1, 1, 1 }
        },
        {
            { 0, 1, 0 },
            { 0, 1, 0 },
            { 0, 1, 1 }
        }
    },

    J = {
        {
            { 1, 0, 0 },
            { 1, 1, 1 }
        },
        {
            { 0, 1, 1 },
            { 0, 1, 0 },
            { 0, 1, 0 }
        },
        {
            { 0, 0, 0 },
            { 1, 1, 1 },
            { 0, 0, 1 }
        },
        {
            { 0, 1 },
            { 0, 1 },
            { 1, 1 }
        }
    },

    O = {
        {
            { 1, 1 },
            { 1, 1 }
        },
        {
            { 1, 1 },
            { 1, 1 }
        },
        {
            { 1, 1 },
            { 1, 1 }
        },
        {
            { 1, 1 },
            { 1, 1 }
        }
    },

    I = {
        {
            { 0, 0, 0, 0 },
            { 1, 1, 1, 1 }
        },
        {
            { 0, 1 },
            { 0, 1 },
            { 0, 1 },
            { 0, 1 }
        },
        {
            { 0, 0, 0, 0 },
            { 1, 1, 1, 1 }
        },
        {
            { 0, 1 },
            { 0, 1 },
            { 0, 1 },
            { 0, 1 }
        }
    },

    S = {
        {
            { 0, 0, 0 },
            { 0, 1, 1 },
            { 1, 1, 0 }
        },
        {
            { 0, 1, 0 },
            { 0, 1, 1 },
            { 0, 0, 1 }
        },
        {
            { 0, 0, 0 },
            { 0, 1, 1 },
            { 1, 1, 0 }
        },
        {
            { 0, 1, 0 },
            { 0, 1, 1 },
            { 0, 0, 1 }
        }
    },

    Z = {
        {
            { 0, 0, 0 },
            { 1, 1, 0 },
            { 0, 1, 1 }
        },
        {
            { 0, 0, 1 },
            { 0, 1, 1 },
            { 0, 1, 0 }
        },
        {
            { 0, 0, 0 },
            { 1, 1, 0 },
            { 0, 1, 1 }
        },
        {
            { 0, 0, 1 },
            { 0, 1, 1 },
            { 0, 1, 0 }
        }
    }
}

COLS = 10
ROWS = 20

function GameLogic:init(params)
	Behaviour.init(self, params)
	print("GameLogic:init")
	for key, value in pairs(params) do
		print(key, value)
	end
	self.gridMinos = params.gridMinos
	self.tetrimino = params.tetrimino
	self.holder = params.holder
	self.pattern = params.pattern

	for i = 1, 5 do
		for j = 1, 5 do
		local p = GameObject:create(self.pattern)
		local pos = p:getComponent("Transform"):getPosition()
		pos.x = (i-1) * 192
		pos.y = (j-1) * 225
params._bg:addChild(p)
	end
	end

	self.grid = {
		[ 1] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
		[ 2] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
		[ 3] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
		[ 4] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
		[ 5] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
		[ 6] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
		[ 7] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
		[ 8] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
		[ 9] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
		[10] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
		[11] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
		[12] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
		[13] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
		[14] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
		[15] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
		[16] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
		[17] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
		[18] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
		[19] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
		[20] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
	}
	self.offsetX = 265
	self.offsetY = 685
	self.size = 30
	self.rotation = 1
	self:spawnNewBlock()
end

function GameLogic:spawnNewBlock()
	print("Spawn new block")
	-- Seed the random number generator once (at program start)
	math.randomseed(os.time())

	-- Get all keys from the TETRIS_PEACES table
	local keys = {}
	for k in pairs(TETRIMINOS) do
	    table.insert(keys, k)
	end

	-- Pick a random piece name
	local randomKey = keys[math.random(#keys)]

	self.currentPeace = randomKey
	self.rotation = 1
	self.cursor = self:getDefaultCursor()
	print("Default Cursor:", self.cursor.x, ":", self.cursor.y)
	self.rotation = 2
	local p = self:getCurrentPeace()
	self.blocks = {}
	self.holder:removeAllChildren()
	for i = 1, #p do
		for j = 1, #p[i] do
			if (p[i][j] == 1) then
				print(i, j)
				local block = GameObject:create(self.tetrimino)
				local pos = block:getComponent("Transform"):getPosition()
				pos.x = ((j-1) * self.size)
				pos.y = ((i-1) * self.size) * -1
				self.holder:addChild(block)
				table.insert(self.blocks, block)
			end
		end
	end
	local hp = self.holder:getComponent("Transform"):getPosition()
	hp.x = self.offsetX + (self.size * (self.cursor.x - 1))
	hp.y = self.offsetY - (self.size * (self.cursor.y - 1))
end

function GameLogic:getDefaultCursor()
	return {
		x = math.ceil(COLS / 2 - #self:getCurrentPeace() / 2) ,
		y = 1
	}
end

--- @brief Get current tetrimino.
function GameLogic:getCurrentPeace()
	return TETRIMINOS[self.currentPeace][self.rotation]
end

--- @brief Move current tetrimino on x axis.
--- @param dt number - Delta time.
function GameLogic:move_on_x_axis(dt)
	if Input:isKeyJustPressed(KEY_A) and (not self:check_collision_x("Left")) then
		self.cursor.x = self.cursor.x - 1
	end

	if Input:isKeyJustPressed(KEY_D) and (not self:check_collision_x("Right")) then
		self.cursor.x = self.cursor.x + 1
	end
end

--- @brief Move current tetrimino on y axis.
--- @param dt number - Delta time.
t = 0
s = false
function GameLogic:move_on_y_axis(dt)
	t = t+dt
	if  t > 0.05 then
		s = true
	else
		s = false
	end
	if s==true and Input:isKeyPressed(KEY_S) and (not self:check_collision_y()) then
		self.cursor.y = self.cursor.y + 1
		s = false
		t = 0
		if self:check_collision_y() then
		self:place_tetrimino()

		end
	end

end

--- @brief Check if current tetrimino collides on x axis.
--- @param size string - Left or Right side.
--- @return boolean
function GameLogic:check_collision_x(side)
	local tet = self:getCurrentPeace()
	for i = 1, #tet do
		for j = 1, #tet[i] do
			if side == "Left"  and (tet[i][j] == 1) and (self.cursor.x + j - 1 == 1 or self.grid[self.cursor.y + i - 1][self.cursor.x + j - 2] == 1) then
				return true
			end
			if side == "Right" and (tet[i][j] == 1) and (self.cursor.x + j + 1 == 12 or self.grid[self.cursor.y + i - 1][self.cursor.x + j] == 1) then
				return true
			end
		end
	end
	return false
end

--- @brief Check if current tetrimino collides on y axis.
--- @return boolean
function GameLogic:check_collision_y()
	local tet = self:getCurrentPeace()
	for i = 1, #tet do
		for j = 1, #tet[i] do
			if (tet[i][j] == 1) and (self.cursor.y + i == 21 or self.grid[self.cursor.y + i][self.cursor.x + j - 1] == 1) then
				return true
			end
		end
	end
	return false
end

function GameLogic:rotate()
	if not Input:isKeyJustPressed(KEY_W) then
		return
	end
	local old_rot = self.rotation
	self.rotation = self.rotation + 1
	if (self.rotation > 4) then
		self.rotation = 1
	end
	
	local t = self:getCurrentPeace()
	if self.cursor.y + #t > 21 then
		self.rotation = old_rot
		return
	end

	self.holder:removeAllChildren()
	local p = self:getCurrentPeace()
	self.blocks = {}
	for i = 1, #p do
		for j = 1, #p[i] do
			if (p[i][j] == 1) then
				local block = GameObject:create(self.tetrimino)
				local pos = block:getComponent("Transform"):getPosition()
				pos.x = ((j-1) * self.size)
				pos.y = ((i-1) * self.size) * -1
				self.holder:addChild(block)
				table.insert(self.blocks, block)
			end
		end
	end

	local old_x = self.cursor.x
	if (self.cursor.x < 1) then
		self.cursor.x = 1
	end

    if (self.cursor.x + #p[1] == 12) then
		self.cursor.x = 11 - #p[1]
	end
end

function GameLogic:place_tetrimino()
    local p = self:getCurrentPeace()
    for i = 1, #p do
    	for j = 1, #p[i] do
    		if p[i][j] == 1 then
    			self.grid[self.cursor.y + i - 1][self.cursor.x + j - 1] = 1
    			local mino = GameObject:create(self.tetrimino)
    			self.gridMinos:addChild(mino)
    			local pos = mino:getComponent("Transform"):getPosition()
    			pos.x = (self.cursor.x + j - 2) * self.size
    			pos.y = (self.cursor.y + i - 2) * self.size * -1
    		end
    	end
   	end
   	self:spawnNewBlock()
end

function GameLogic:check_for_cleared_lines()
	for i = ROWS, 1, -1 do
		local sum = 0
		for j = 1, #self.grid[i] do
			sum = sum + self.grid[i][j]
		end
		if (sum == 10) then
						-- Remove the full row
			table.remove(self.grid, i)

			-- Insert a new empty row at the top
			local empty_row = {}
			for _ = 1, COLS do
				table.insert(empty_row, 0)
			end
			table.insert(self.grid, 1, empty_row)

			print("FODIUN")
		end
	end
end

function GameLogic:update(dt)
	local hp = self.holder:getComponent("Transform"):getPosition()
	hp.x = self.offsetX + (self.size * (self.cursor.x - 1))
	hp.y = self.offsetY - (self.size * (self.cursor.y - 1))

	if Input:isKeyJustPressed(KEY_SPACE) then
		print("CURSOR: X:", self.cursor.x, " Y:", self.cursor.y, " R:", self.rotation)
		for i = 1, 20 do
			local row = ""
			for j = 1, 10 do
				row = row .. " " .. self.grid[i][j]
			end
			print(row)
		end
	end

	self:move_on_x_axis(dt)
	self:move_on_y_axis(dt)
	self:rotate()
	self:check_for_cleared_lines()

	 if (Input:isKeyJustPressed(KEY_R)) then
	 	AudioManager:play(self.params.music)
	 end

end

return GameLogic











































